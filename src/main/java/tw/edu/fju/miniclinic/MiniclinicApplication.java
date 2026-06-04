package tw.edu.fju.miniclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCrypt;

@SpringBootApplication
public class MiniclinicApplication {

	public static void main(String[] args) {
		// 臨時加入這行來印出密碼的雜湊值，產生完後可以刪除
		System.out.println("產生的 BCrypt 雜湊值: " + BCrypt.hashpw("pass1234", BCrypt.gensalt()));

		SpringApplication.run(MiniclinicApplication.class, args);
	}

}
