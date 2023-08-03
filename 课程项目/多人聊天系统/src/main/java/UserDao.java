import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;



public class UserDao {
    private final JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());  //创建JdbcTemplate对象,依赖于DataSource


    public User login(User login_user) {
        try {
            //编写sql
            String sql = "select * from user where username = ? and password = ?";


            User user = template.queryForObject(sql,
                    new BeanPropertyRowMapper<User>(User.class),
                    login_user.getUsername(), login_user.getPassword());
            return user;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int register(User register_user) {
        try {
            String sql = "insert into user values (?,?)";
            //返回行数
            int count = template.update(sql,register_user.getUsername(),register_user.getPassword());
            return count;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

}


