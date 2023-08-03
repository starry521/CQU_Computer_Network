import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class JDBCUtils {
    private static DataSource ds;

    static {
        try {
            Properties pro = new Properties();

            //采用类加载器ClassLoader得到properties文件的的输入流
            InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("druid.properties");
            //读取 Properties
            pro.load(is);
            //获取连接池对象
            ds = DruidDataSourceFactory.createDataSource(pro);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static DataSource getDataSource(){
        return ds;
    }


}


