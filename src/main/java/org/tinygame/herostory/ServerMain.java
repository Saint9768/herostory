package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.CmdHandlerFactory;

/**
 * 服务器入口类
 */
public class ServerMain {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    /**
     * 服务器端口号
     */
    static final int SERVER_PORT = 12345;

    /**
     * 应用主函数
     *
     * @param argArray 参数数组
     */
    static public void main(String[] argArray) {
        // 设置log4j属性文件
        PropertyConfigurator.configure(ServerMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        // 初始化命令工厂
        CmdHandlerFactory.init();

        // 初始化命令識別器
        GameMsgRecognizer.init();
        // 初始化 MySql 会话工厂
        MySqlSessionFactory.init();

        // 配置服务端的NIO线程组，相当于是BOSS.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 是worker线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //对Server做一些启动之前的配置
        ServerBootstrap b = new ServerBootstrap();
        //执行两个线程组，第一个负责连接，第二个用于进行SocketChannel的网络读写
        b.group(bossGroup, workerGroup)
                // 服务器信道的处理方式
                .channel(NioServerSocketChannel.class)
                //作用类似于Reactor模式中的handler类，主要用于处理网络I/O事件。
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    //通道初始化之后，给通道添加处理器
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new HttpServerCodec(), // Http 服务器编解码器
                                new HttpObjectAggregator(65535), // 内容长度限制
                                new WebSocketServerProtocolHandler("/websocket"), // WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                                new GameMsgDecoder(), // 自定义的消息解码器
                                new GameMsgEncoder(), // 自定义的消息编码器
                                new GameMsgHandler() // 自定义的消息处理器
                        );
                    }
                });

        try {
            // 绑定 12345 端口
            // 注意: 实际项目中会使用 argArray 中的参数来指定端口号
            ChannelFuture f = b.bind(SERVER_PORT).sync();

            if (f.isSuccess()) {
                LOGGER.info("服务器启动成功!");
            }

            // 等待服务器信道关闭,
            // 也就是不要立即退出应用程序, 让应用程序可以一直提供服务
            f.channel().closeFuture().sync();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            //释放线程池资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
