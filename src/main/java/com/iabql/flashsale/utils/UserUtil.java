package com.iabql.flashsale.utils;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iabql.flashsale.pojo.User;
import com.iabql.flashsale.service.impl.UserServiceImpl;
import com.iabql.flashsale.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生成用户工具类
 *
 * @author: LC
 * @date 2022/3/4 3:29 下午
 * @ClassName: UserUtil
 */
@Controller
public class UserUtil {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/test/{count}")
    @ResponseBody
    private void  createUser(@PathVariable int count) throws SQLException, ClassNotFoundException, IOException {
        List<User> users = new ArrayList<>(count);
        for (int i = 0;i<count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("user" + i);
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            users.add(user);
        }
            /*Connection con = getCon();
            String sql = "insert into t_user (login_count,nickname,register_date,salt,password,id) values(?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            for (User user1 : users) {
                pst.setInt(1,user1.getLoginCount());
                pst.setString(2,user1.getNickname());
                pst.setTimestamp(3,new Timestamp(user1.getRegisterDate().getTime()));
                pst.setString(4,user1.getSalt());
                pst.setString(5,user1.getPassword());
                pst.setLong(6,user1.getId());
                pst.addBatch();
            }
            pst.executeBatch();
            pst.clearParameters();
            con.close();
            System.out.println("insert to db");*/

        //登录，生成token
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\ASUS\\Desktop\\config.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" +
                    MD5Util.inputPasswordToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = ((String) respBean.getObject());

            //todo 存入redis
            userService.addLoginTicket(userTicket,JSON.toJSONString(user));
            System.out.println("create userTicket : " + user.getId());
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();
        System.out.println("over");
        }



    private Connection getCon() throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/flashsale?characterEncoding=utf-8&userSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "123456";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url,username,password);
    }

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        //createUser(5000);
    }
}
