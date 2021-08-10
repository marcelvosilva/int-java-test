package br.com.inter.testejava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.inter.testejava.cache.Cache;
import br.com.inter.testejava.controller.TesteController;
import br.com.inter.testejava.dto.Resultados;
import br.com.inter.testejava.dto.Usuario;
import br.com.inter.testejava.security.Crypto;
import br.com.inter.testejava.service.DigitoUnicoService;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class TesteJavaApplicationTests {

	@Autowired
	TesteController tC;

	@Autowired
	DigitoUnicoService dU;

	@Autowired
	Cache cache;

	@Autowired
	Crypto crypto;

	@Test
	@Order(1)
	public void testDigitoUnico() throws Exception {
		// Testes de parâmetros inválidos
		Throwable e = assertThrows(Exception.class, () -> dU.uniqueDigit("0", 1));
		Throwable e1 = assertThrows(Exception.class, () -> dU.uniqueDigit("1", 0));
		assertEquals("Parâmetro inválido!", e.getMessage());
		assertEquals("Parâmetro inválido!", e1.getMessage());

		// Teste de resultado
		assertEquals(1, dU.uniqueDigit("1", 1));
		assertEquals(2, dU.uniqueDigit("2", 1));
		assertEquals(3, dU.uniqueDigit("3", 1));
		assertEquals(2, dU.uniqueDigit("11", 1));
		assertEquals(4, dU.uniqueDigit("22", 1));
		assertEquals(1, dU.uniqueDigit("55", 1));
		assertEquals(3, dU.uniqueDigit("222", 2));
		assertEquals(4, dU.uniqueDigit("1505", 2));
		assertEquals(1, dU.uniqueDigit("11111", 2));
		assertEquals(2, dU.uniqueDigit("40", 5));
	}

	@Test
	@Order(2)
	public void testCache() {
		cache.put("1,1", "1");
		cache.put("2,1", "2");
		cache.put("3,1", "3");
		cache.put("4,1", "4");
		cache.put("5,1", "5");
		cache.put("6,1", "6");
		cache.put("7,1", "7");
		cache.put("8,1", "8");
		cache.put("9,1", "9");
		cache.put("10,1", "10");

		assertEquals("1", cache.get("1,1"));
		cache.put("11", "11,1");
		assertEquals(null, cache.get("1,1"));
	}

	@Test
	@Order(3)
	public void testGetAllUsuarios() {
		tC.createUsuario(new Usuario("Marcel", "teste@gmail.com"));
		tC.createUsuario(new Usuario("Lucas", "teste@gmail.com"));
		tC.createUsuario(new Usuario("André", "teste@gmail.com"));
		ResponseEntity<List<Usuario>> response = tC.getAllUsuarios();
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	@Order(4)
	public void testAddChavePublica() {
		String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnWL+KtbPTOe8rJtXQi0oVRmAX9AJ/Wl5HLLxUg56yq4cggx8EXlNJoQXB6Lymv9dWEGShNeLDfvfDAhjSy01fZeXZL2alfxGli3bZmvn1tRuLz3x8AxK06Ps8iJ69GMGILryRh5hRIqG2r1+epDmkMAAW/9/bqWxnmTPfSgs2Wily5oDzYxDE+7TmhTaoEFAfk1tFwKtf48usofvqa/xtLxh1HKrcCI8hb9yqs5AaNEH+gzZBOuf/GHZpKLHjVmgvcev+MdXz8fkEyAxRkiS0CBAuvp/gPbW5Cs+YHW94F5xDKrjh1tmeOZCQo/Q1tQlk2F4CbYg3D5NfXhYLngEtQIDAQAB";
		ResponseEntity<String> response = tC.addPubKey(pubKey, 1);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		ResponseEntity<String> response2 = tC.addPubKey(pubKey, 2);
		assertEquals(HttpStatus.CONFLICT, response2.getStatusCode());
	}

	@Test
	@Order(5)
	public void testGetUsuarioById() {
		ResponseEntity<Usuario> response = tC.getUsuarioById(null, 1);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		ResponseEntity<Usuario> response2 = tC.getUsuarioById(
				"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCdYv4q1s9M57ysm1dCLShVGYBf0An9aXkcsvFSDnrKrhyCDHwReU0mhBcHovKa/11YQZKE14sN+98MCGNLLTV9l5dkvZqV/EaWLdtma+fW1G4vPfHwDErTo+zyInr0YwYguvJGHmFEiobavX56kOaQwABb/39upbGeZM99KCzZaKXLmgPNjEMT7tOaFNqgQUB+TW0XAq1/jy6yh++pr/G0vGHUcqtwIjyFv3KqzkBo0Qf6DNkE65/8YdmkoseNWaC9x6/4x1fPx+QTIDFGSJLQIEC6+n+A9tbkKz5gdb3gXnEMquOHW2Z45kJCj9DW1CWTYXgJtiDcPk19eFgueAS1AgMBAAECggEAZmqfyRLGDV6TqaLgkoC+STpyDmdbyNsU4/cSEHq5ryDEDmC/b8OGz/SO4/pGc1SP0rnA9c8pfu0TRUpECwydTWATNYwoh0eGS34F0s2PLX5JQVbHFJV7L+PxcdO8PUIfH7dnpvdoBmk6vicnoeyfDTzUPwmxTDhosBEb8G7vWH23vTn06uIPW8Z0LxV1xq5F6rCpf+l8dZVjQQLeYHty+YojhrqKS+O+u5KhemVvu2GAZ5IwP+t1rIf+6IfZMe5UmMhGaleMg0x6+1Av1AJHHArGKhpcfHHETl3CBn0virKWjpat/tu7fX+h6sJ5+1siX0mH/NPKKUA58kBzI5KitQKBgQDoHKGrAP+OhrHOUQnSy7ujLi5XOOZTJ0PlAPOekHfequd5NGGcuQ8pD0QQo7alsxyGlIW2OJs2xtgS5kq9WBWZy7pRMq5ijpVMwW7XZ1KFQqjqJY7dU+/76P/3DCWtKUBdFJBdnGacqMw8fnFEASpaSLVf92LDe87ge1u9pN1+SwKBgQCtlZqvUV98KB3rqYuzY230Oi2G5XI7ddKXWVypreJ1zU+kD/zhzMMjssoiBqqhHB3tZd9aigVza343549z+/ExGKUQoEfAHn1alyG76C5GLkfu4W+zKKxaab7iYqVmg3gKOcEjbuSEzDYMBMRLSRDVTspsouvv6PW5ewqFXyKo/wKBgDcR2/WThg6NvI/jwD3oInuj0FDXaiYecchagnKDej01NTgXinVBnuwx+AcuoH4N0d3ITTUflwvW/5r0UpU72dwkbu8m5CL0C04rsp2eoQCHAUaUZKH0cULGVDyMAEBmQiQTZfEf02Sl09cidMMat662A3piWmdg74NpISoMfJSjAoGAbIPcAcGzGaDSo6KITQrZkBZWo8AIm5daoXch8IldyxsgsIW2J0qkq0p4BrF4NeOn6eMHsEgchD0s2LIQAgf8jh7EAhLRCA+HefyFAD1zwDfqjDGubKf+pz74L/pPQDjr8CiOzyHiBgrLttO8CvyQY2kwLp3jNUXDZ8K9nwcMWe0CgYBp0n8b3wbN8LqT0909zRg/1Gx6nmF6sO72TgDmnmSvEedL11YcfdyWQykL1ctSiomvmjPW+F0Tl3zcjz3Xci884+tc1Y6a37n6QQpvTspVPx7PDQ0UOvoKRrujAian42lbRPbpMaSdtM+T4g6Ce+ocV49wqAbzaeG/kWMuV6h1AA==",
				1);
		assertEquals(HttpStatus.OK, response2.getStatusCode());
	}

	@Test
	@Order(6)
	public void testGetUsuarioDigitosById() {
		ResponseEntity<List<String>> response = tC.getUsuarioDigitosById(1);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	@Order(7)
	public void testCreateUsuario() {
		Usuario usuario = new Usuario("Maria", "teste@gmail.com");
		ResponseEntity<String> response = tC.createUsuario(usuario);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	@Order(8)
	public void testUpdateUsuario() {
		tC.createUsuario(new Usuario("Marcel", "teste@gmail.com"));
		Usuario usuario2 = new Usuario("João", "teste2@gmail.com");
		ResponseEntity<String> response = tC.updateUsuario(1, usuario2);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	@Order(9)
	public void testCalculoDigito() {
		ResponseEntity<Integer> response = tC.calculoDigito(new Resultados("1", 1), null);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		ResponseEntity<Integer> response2 = tC.calculoDigito(new Resultados("1", 1), 2);
		assertEquals(HttpStatus.OK, response2.getStatusCode());
	}

	@Test
	@Order(10)
	public void testDeleteUsuario() {
		ResponseEntity<String> response = tC.deleteUsuario(3);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	@Order(11)
	public void testDeleteAllUsuarios() {
		ResponseEntity<String> response = tC.deleteAllUsuarios();
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	@Order(12)
	public void testCriptografia() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
			NoSuchPaddingException, NoSuchAlgorithmException {
		byte[] criptografado = crypto.encrypt("Marcel", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnWL+KtbPTOe8rJtXQi0oVRmAX9AJ/Wl5HLLxUg56yq4cggx8EXlNJoQXB6Lymv9dWEGShNeLDfvfDAhjSy01fZeXZL2alfxGli3bZmvn1tRuLz3x8AxK06Ps8iJ69GMGILryRh5hRIqG2r1+epDmkMAAW/9/bqWxnmTPfSgs2Wily5oDzYxDE+7TmhTaoEFAfk1tFwKtf48usofvqa/xtLxh1HKrcCI8hb9yqs5AaNEH+gzZBOuf/GHZpKLHjVmgvcev+MdXz8fkEyAxRkiS0CBAuvp/gPbW5Cs+YHW94F5xDKrjh1tmeOZCQo/Q1tQlk2F4CbYg3D5NfXhYLngEtQIDAQAB");
		assertEquals("Marcel", crypto.decrypt(criptografado, "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCdYv4q1s9M57ysm1dCLShVGYBf0An9aXkcsvFSDnrKrhyCDHwReU0mhBcHovKa/11YQZKE14sN+98MCGNLLTV9l5dkvZqV/EaWLdtma+fW1G4vPfHwDErTo+zyInr0YwYguvJGHmFEiobavX56kOaQwABb/39upbGeZM99KCzZaKXLmgPNjEMT7tOaFNqgQUB+TW0XAq1/jy6yh++pr/G0vGHUcqtwIjyFv3KqzkBo0Qf6DNkE65/8YdmkoseNWaC9x6/4x1fPx+QTIDFGSJLQIEC6+n+A9tbkKz5gdb3gXnEMquOHW2Z45kJCj9DW1CWTYXgJtiDcPk19eFgueAS1AgMBAAECggEAZmqfyRLGDV6TqaLgkoC+STpyDmdbyNsU4/cSEHq5ryDEDmC/b8OGz/SO4/pGc1SP0rnA9c8pfu0TRUpECwydTWATNYwoh0eGS34F0s2PLX5JQVbHFJV7L+PxcdO8PUIfH7dnpvdoBmk6vicnoeyfDTzUPwmxTDhosBEb8G7vWH23vTn06uIPW8Z0LxV1xq5F6rCpf+l8dZVjQQLeYHty+YojhrqKS+O+u5KhemVvu2GAZ5IwP+t1rIf+6IfZMe5UmMhGaleMg0x6+1Av1AJHHArGKhpcfHHETl3CBn0virKWjpat/tu7fX+h6sJ5+1siX0mH/NPKKUA58kBzI5KitQKBgQDoHKGrAP+OhrHOUQnSy7ujLi5XOOZTJ0PlAPOekHfequd5NGGcuQ8pD0QQo7alsxyGlIW2OJs2xtgS5kq9WBWZy7pRMq5ijpVMwW7XZ1KFQqjqJY7dU+/76P/3DCWtKUBdFJBdnGacqMw8fnFEASpaSLVf92LDe87ge1u9pN1+SwKBgQCtlZqvUV98KB3rqYuzY230Oi2G5XI7ddKXWVypreJ1zU+kD/zhzMMjssoiBqqhHB3tZd9aigVza343549z+/ExGKUQoEfAHn1alyG76C5GLkfu4W+zKKxaab7iYqVmg3gKOcEjbuSEzDYMBMRLSRDVTspsouvv6PW5ewqFXyKo/wKBgDcR2/WThg6NvI/jwD3oInuj0FDXaiYecchagnKDej01NTgXinVBnuwx+AcuoH4N0d3ITTUflwvW/5r0UpU72dwkbu8m5CL0C04rsp2eoQCHAUaUZKH0cULGVDyMAEBmQiQTZfEf02Sl09cidMMat662A3piWmdg74NpISoMfJSjAoGAbIPcAcGzGaDSo6KITQrZkBZWo8AIm5daoXch8IldyxsgsIW2J0qkq0p4BrF4NeOn6eMHsEgchD0s2LIQAgf8jh7EAhLRCA+HefyFAD1zwDfqjDGubKf+pz74L/pPQDjr8CiOzyHiBgrLttO8CvyQY2kwLp3jNUXDZ8K9nwcMWe0CgYBp0n8b3wbN8LqT0909zRg/1Gx6nmF6sO72TgDmnmSvEedL11YcfdyWQykL1ctSiomvmjPW+F0Tl3zcjz3Xci884+tc1Y6a37n6QQpvTspVPx7PDQ0UOvoKRrujAian42lbRPbpMaSdtM+T4g6Ce+ocV49wqAbzaeG/kWMuV6h1AA=="));
	}
}
