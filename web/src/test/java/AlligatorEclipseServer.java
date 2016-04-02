package java;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.util.Log4jConfigurer;

/**
 * Jetty嵌入式服务器引导程序
 */
public class AlligatorEclipseServer {
	private static final int PORT = 8080;
	private static final String CONTEXT_PATH = "/";
	private static final String WEBAPP_RELATIVE_PATH = "./src/main/webapp";
	private static final String DEFAULT_DISCRIPTOR_PATH = "./src/test/resources/webdefault.xml";
	
	private static final String CLASS_PATH = AlligatorEclipseServer.class.getResource("/").getPath();

	/**
	 * 创建用于开发运行调试的JettyServer
	 * 
	 * @param port
	 *            访问服务器的端口
	 * @param contextPath
	 *            访问服务器的地址
	 * @param webAppRelativePath
	 *            Web应用的相对目录(需指向到WebRoot目录下)
	 */
	private static Server createServer(int port, String contextPath,
			String webAppRelativePath) {
		Server server = new Server();
		// 设置在JVM退出时关闭Jetty的钩子
		// 这样就可以在整个功能测试时启动一次Jetty,然后让它在JVM退出时自动关闭
		server.setStopAtShutdown(true);
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		// Sun的connector实现的问题,reuseAddress=true时重复启动同一个端口的Jetty不会报错
		// 所以必须设为false,代价是若上次退出不干净(比如有TIME_WAIT),会导致新的Jetty不能启动,但权衡之下还是应该设为False
		connector.setReuseAddress(false);
		server.setConnectors(new Connector[] { connector });
		// 为了设置reuseAddress=true所以创建Connector,否则直接new
		// Server(port)即可,通过查看Server源码发现,二者是等效的
		// 不过使用Connector的好处是可以让Jetty监听多个端口,此时创建多个绑定不同端口的Connector即可,最后一起setConnectors
		WebAppContext context = new WebAppContext();
		context.setContextPath(contextPath);
		context.setResourceBase(webAppRelativePath);
		// 解决Windows环境下Lock静态文件的问题
		// Jetty运行时会锁住js、css等文件,导致不能修改保存,解决办法是修改webdefault.xml中的useFileMappedBuffer=false，
		// 不使用jetty默认的
		context.setDefaultsDescriptor(DEFAULT_DISCRIPTOR_PATH);
		server.setHandler(context);
		return server;
	}

	/**
	 * 快速重新启动Application
	 * 
	 * @see 通常用Main函数启动JettyServer后,若改动项目的代码,那就需要停止再启动Jetty
	 * @see 虽免去了Tomcat重新打包几十兆的消耗，但比起PHP完全不用重启来说还是慢,特别是关闭,启动一个新的JVM,消耗不小
	 * @see 所以我们可以在Main()中捕捉到回车后调用此函数,即可重新载入应用(包括Spring配置文件)
	 * @param server
	 *            当前运行的JettyServer实例
	 * @param classPath
	 *            当前运行的Web应用的classpath
	 */
	private static synchronized void reloadContext(Server server,
			String classPath) throws Exception {
		WebAppContext context = (WebAppContext) server.getHandler();
		System.out.println("Application reloading..开始");
		context.stop();
		WebAppClassLoader classLoader = new WebAppClassLoader(context);
		classLoader.addClassPath(classPath);
		context.setClassLoader(classLoader);
		// 根据给定的配置文件初始化日志配置(否则应用重载后日志输出组件就会失效)
		Log4jConfigurer.initLogging(classPath + "/logback.xml");
		context.start();
		System.out.println("Application reloading..完毕");
		printTips();
	}

	public static void main(String[] args) {
		Server server = createServer(PORT, CONTEXT_PATH, WEBAPP_RELATIVE_PATH);

		try {
			// 启动Jetty
			server.start();
			printTips();
			// 等待用户键入回车重载应用
			while (true) {
				char c = (char) System.in.read();
				if (c == '\n') {
					reloadContext(server, CLASS_PATH);
				}
			}
		} catch (Exception e) {
			System.out.println("Jetty启动失败,堆栈轨迹如下");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static void printTips() {
		try {
			java.net.InetAddress addr = java.net.Inet4Address.getLocalHost();
			System.out.println("Server running at (127.0.0.1) http://" + addr.getHostAddress() + ":" + PORT
					+ CONTEXT_PATH);
			System.out.println("Hit Enter to reload the application quickly");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}