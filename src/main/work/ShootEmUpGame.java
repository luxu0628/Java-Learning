package main.work;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 单文件版——打飞机（Swing GUI）示例
 *
 * 特性：
 * - 使用 Swing 绘制（JFrame + JPanel）
 * - 主游戏循环使用 javax.swing.Timer（UI 线程安全）
 * - 敌机生成使用单独的调度线程（ScheduledExecutorService）以体现多线程控制
 * - 子弹/敌机使用 CopyOnWriteArrayList 保证并发安全
 * - 碰撞检测、分数、生命、简单爆炸动画、关卡机制
 *
 * 运行方法：在 IDEA 中新建一个 Java 项目，把本文件放到 src/ 下，运行 main()。
 */
public class ShootEmUpGame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}

class GameFrame extends JFrame {
    public GameFrame() {
        setTitle("打飞机 - Java Swing 版");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        GamePanel panel = new GamePanel(480, 720);
        add(panel);
        pack();
        setLocationRelativeTo(null);
        panel.startGame();
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int width;
    private final int height;

    // 游戏状态
    private volatile boolean running = false;
    private volatile boolean paused = false;

    // 玩家
    private Player player;

    // 对象列表（线程安全）
    private final List<Bullet> bullets = new CopyOnWriteArrayList<>();
    private final List<Enemy> enemies = new CopyOnWriteArrayList<>();
    private final List<Explosion> explosions = new CopyOnWriteArrayList<>();

    // 计时器（UI 线程）
    private final Timer gameTimer;

    // 敌机生成线程池
    private final ScheduledExecutorService enemySpawner = Executors.newSingleThreadScheduledExecutor();

    // 随机数
    private final Random rand = new Random();

    // 游戏参数
    private int score = 0;
    private int level = 1;
    private int enemySpawnIntervalMs = 1200; // 初始生成间隔

    public GamePanel(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        player = new Player(width / 2 - 20, height - 100);

        // 游戏循环：每 16ms (~60 FPS) 更新并重绘
        gameTimer = new Timer(16, this);
    }

    public void startGame() {
        running = true;
        paused = false;
        score = 0;
        level = 1;
        enemySpawnIntervalMs = 1200;
        enemies.clear();
        bullets.clear();
        explosions.clear();
        player.reset(width / 2 - 20, height - 100);

        // 启动敌机生成线程
        enemySpawner.scheduleAtFixedRate(this::spawnEnemy, 500, enemySpawnIntervalMs, TimeUnit.MILLISECONDS);

        gameTimer.start();
    }

    public void stopGame() {
        running = false;
        gameTimer.stop();
        enemySpawner.shutdownNow();
    }

    private void spawnEnemy() {
        if (!running || paused) return;
        // 根据关卡调整敌机类型和速度
        int x = rand.nextInt(Math.max(1, width - 40));
        int hp = Math.min(3 + level / 2, 6);
        int speed = Math.min(1 + level / 2 + rand.nextInt(2), 6);
        Enemy e = new Enemy(x, -40, hp, speed);
        enemies.add(e);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // 绘制玩家
        player.draw(g2);

        // 绘制子弹
        for (Bullet b : bullets) b.draw(g2);

        // 绘制敌机
        for (Enemy e : enemies) e.draw(g2);

        // 绘制爆炸
        for (Explosion ex : explosions) ex.draw(g2);

        // UI：分数与生命
        drawHUD(g2);

        if (!running) {
            drawCenteredString(g2, "游戏结束 - 按 R 重玩", new Rectangle(0, 0, width, height), new Font("Arial", Font.BOLD, 28), Color.WHITE);
        } else if (paused) {
            drawCenteredString(g2, "已暂停 - 按 P 恢复", new Rectangle(0, 0, width, height), new Font("Arial", Font.BOLD, 28), Color.YELLOW);
        }

        g2.dispose();
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.drawString("分数: " + score, 10, 20);
        g2.drawString("生命: " + player.lives, 10, 40);
        g2.drawString("关卡: " + level, 10, 60);
        g2.drawString("按 ← → 或 A D 移动，空格射击，P 暂停，R 重玩", 10, height - 10);
    }

    private void drawCenteredString(Graphics2D g2, String text, Rectangle rect, Font font, Color color) {
        FontMetrics metrics = g2.getFontMetrics(font);
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.setFont(font);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running || paused) return;

        // 玩家移动更新
        player.update(width);

        // 子弹移动（在主循环中移动）
        for (Bullet b : bullets) {
            b.update();
            if (b.y < -10) bullets.remove(b);
        }

        // 敌机移动
        for (Enemy en : enemies) {
            en.update();
            if (en.y > height + 50) {
                enemies.remove(en);
                player.lives -= 1; // 敌机穿过底部扣生命
                if (player.lives <= 0) {
                    running = false;
                }
            }
        }

        // 碰撞检测：子弹和敌机
        for (Bullet b : bullets) {
            for (Enemy en : enemies) {
                if (b.getBounds().intersects(en.getBounds())) {
                    bullets.remove(b);
                    en.hp -= b.damage;
                    explosions.add(new Explosion(en.x + en.width / 2, en.y + en.height / 2));
                    if (en.hp <= 0) {
                        enemies.remove(en);
                        score += 10;
                        maybeLevelUp();
                    }
                    break; // 子弹消失后跳出外层循环
                }
            }
        }

        // 敌机与玩家碰撞
        for (Enemy en : enemies) {
            if (en.getBounds().intersects(player.getBounds())) {
                enemies.remove(en);
                explosions.add(new Explosion(player.x + player.width / 2, player.y + player.height / 2));
                player.lives -= 1;
                if (player.lives <= 0) running = false;
            }
        }

        // 爆炸动画更新
        for (Explosion ex : explosions) {
            ex.update();
            if (!ex.active) explosions.remove(ex);
        }

        repaint();
    }

    private void maybeLevelUp() {
        int newLevel = 1 + score / 100;
        if (newLevel > level) {
            level = newLevel;
            // 提升难度：缩短生成间隔（但不要太短）
            enemySpawnIntervalMs = Math.max(300, enemySpawnIntervalMs - 150);
            // 重新调度敌机生成
            enemySpawner.schedule(() -> {
                // cancel previous scheduling and start a new one using the updated interval
                enemySpawner.scheduleAtFixedRate(this::spawnEnemy, enemySpawnIntervalMs, enemySpawnIntervalMs, TimeUnit.MILLISECONDS);
            }, 1, TimeUnit.MILLISECONDS);
        }
    }

    // 键盘事件
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) player.moveLeft = true;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) player.moveRight = true;
        if (key == KeyEvent.VK_SPACE) player.shooting = true;
        if (key == KeyEvent.VK_P) paused = !paused;
        if (key == KeyEvent.VK_R && !running) startGame();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) player.moveLeft = false;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) player.moveRight = false;
        if (key == KeyEvent.VK_SPACE) player.shooting = false;
    }

    // ---- 内部类：Player, Bullet, Enemy, Explosion ----
    static class Player {
        int x, y, width = 40, height = 40;
        int speed = 6;
        int lives = 3;
        boolean moveLeft = false, moveRight = false;
        boolean shooting = false;

        private long lastShot = 0;
        private final long fireCooldown = 250; // ms

        public Player(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void reset(int x, int y) {
            this.x = x;
            this.y = y;
            this.lives = 3;
            this.moveLeft = this.moveRight = this.shooting = false;
            this.lastShot = 0;
        }

        public void update(int panelWidth) {
            if (moveLeft) x -= speed;
            if (moveRight) x += speed;
            x = Math.max(0, Math.min(panelWidth - width, x));

            // 自动射击逻辑：按住空格就持续射击（有冷却）
            if (shooting) {
                long now = System.currentTimeMillis();
                if (now - lastShot >= fireCooldown) {
                    lastShot = now;
                    // 生成子弹（通过事件：我们把生成暴露到 GamePanel）
                    // 为了避免循环依赖，这里不直接创建子弹。由外部检查 shooting 并创建子弹。
                }
            }
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        public void draw(Graphics2D g2) {
            // 绘制飞机主体
            g2.setColor(Color.CYAN);
            g2.fillRoundRect(x, y, width, height, 8, 8);
            // 绘制机头
            int[] xs = {x + width / 2, x + width + 6, x + width / 2};
            int[] ys = {y, y + height / 2, y + height};
            g2.setColor(Color.BLUE.darker());
            g2.fillPolygon(xs, ys, 3);
        }
    }

    class Bullet {
        int x, y, w = 6, h = 12;
        int speed = 10;
        int damage = 1;

        public Bullet(int x, int y) {
            this.x = x - w / 2;
            this.y = y;
        }

        public void update() {
            y -= speed;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, w, h);
        }

        public void draw(Graphics2D g2) {
            g2.setColor(Color.YELLOW);
            g2.fillRect(x, y, w, h);
        }
    }

    class Enemy {
        int x, y, width = 36, height = 36;
        int speed = 2;
        int hp = 1;

        public Enemy(int x, int y, int hp, int speed) {
            this.x = x;
            this.y = y;
            this.hp = hp;
            this.speed = speed;
        }

        public void update() {
            y += speed;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        public void draw(Graphics2D g2) {
            // 根据生命值改变颜色
            if (hp >= 3) g2.setColor(Color.MAGENTA);
            else if (hp == 2) g2.setColor(Color.ORANGE);
            else g2.setColor(Color.RED);
            g2.fillOval(x, y, width, height);
            // 绘制 HP 微小条
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y - 6, width, 4);
            g2.setColor(Color.GREEN);
            int barW = (int) ((width) * ((double) hp / 4.0));
            g2.fillRect(x + 1, y - 5, Math.max(0, Math.min(width - 2, barW)), 2);
        }
    }

    class Explosion {
        int x, y;
        int life = 12; // 帧数
        boolean active = true;

        public Explosion(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void update() {
            life--;
            if (life <= 0) active = false;
        }

        public void draw(Graphics2D g2) {
            int s = (12 - life) * 4;
            float alpha = Math.max(0, life / 12f);
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2.setComposite(ac);
            g2.setColor(Color.ORANGE);
            g2.fillOval(x - s / 2, y - s / 2, s, s);
            g2.setComposite(AlphaComposite.SrcOver);
        }
    }

    // 为了触发射击（Player 内部只记录 shooting），我们在这里拦截并生成子弹
    // 使用独立方法，避免在 Player.update 中显式创建对象
    {
        // 添加一个小辅助定时器：扫描 player.shooting 并产生子弹（每 100ms）
        Timer shooterTimer = new Timer(100, e -> {
            if (!running || paused) return;
            if (player.shooting) {
                // 子弹从飞机顶部中间发出
                bullets.add(new Bullet(player.x + player.width / 2, player.y));
            }
        });
        shooterTimer.start();
    }
}
