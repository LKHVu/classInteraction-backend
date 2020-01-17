package vgu.vgu;

import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

public class JwtUtil {
	private final String secret;

	JwtUtil() {
		Config config = Config.getConfig();
		secret = config.getSecret();
	}

	public String generateToken(JSONObject loginJson) {
		try {
			JSONObject headerJson = new JSONObject();
			headerJson.put("alg", "HS256");
			headerJson.put("typ", "JWT");
			String header = Base64.getUrlEncoder().encodeToString(headerJson.toString().getBytes());
			String login = loginJson.getString("login");
			String password = loginJson.getString("password");
			sql s = new sql();
			JSONObject payloadJson = s.generatePayload(login, password);
			String payload = Base64.getUrlEncoder().encodeToString(payloadJson.toString().getBytes());
			String signature = encode(secret, header + "." + payload);
			return header + "." + payload + "." + signature;
		} catch (Exception e) {
			e.printStackTrace();
			return "no";
		}	
	}

	public String encode(String key, String data) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
		sha256_HMAC.init(secret_key);
		return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
	}
	
	public boolean validateRole(String token, int role) throws Exception {
		if (token.contains("Bearer")) {
			token = token.replace("Bearer ", "");
			if (validateToken(token)) {
				String payload = token.split("\\.")[1];
				JSONObject payloadJson = new JSONObject(new String(Base64.getUrlDecoder().decode(payload)));
				if (role==payloadJson.getInt("role")) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}	
	}
	
	public boolean validateToken(String token) throws Exception {
		String header = token.split("\\.")[0];
		String payload = token.split("\\.")[1];
		String signature = token.split("\\.")[2];
		if (signature.equals(encode(secret, header + "." + payload))){
			return true;
		} else {
			return false;
		}
	}
}
