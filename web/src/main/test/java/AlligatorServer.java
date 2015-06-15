package java;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class AlligatorServer {

    public static void main(String[] args) {
        Server server = new Server(8082);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/promotion");
        context.setDescriptor("./alligator.web/src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("./alligator.web/src/main/webapp");
        //解决静态资源缓存后再ide里面不能修改问题
        String descriptor = "./alligator.web/src/test/resources/webdefault.xml";
        context.setDefaultsDescriptor(descriptor);
        context.setParentLoaderPriority(true);
        server.setHandler(context);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
