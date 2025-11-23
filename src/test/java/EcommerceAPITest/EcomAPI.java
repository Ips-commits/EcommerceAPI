package EcommerceAPITest;

import io.restassured.builder.RequestSpecBuilder;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import pojo.LoginRequest;
import pojo.LoginResponse;
import pojo.OrderDetails;
import pojo.Orders;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;

public class EcomAPI {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//Login		
		RequestSpecification req=	new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
				.setContentType(ContentType.JSON).build();
		
		LoginRequest loginreq=new LoginRequest();
		loginreq.setUserEmail("ipp@test.com");
		loginreq.setUserPassword("Puglu@1996");
				
		RequestSpecification reqLogin =given().spec(req).body(loginreq);
		
		LoginResponse loginResponse = reqLogin.when().post("/api/ecom/auth/login").then().log().all().extract().response()
				.as(LoginResponse.class);
		
		String token = loginResponse.getToken();
		System.out.println(loginResponse.getToken());
		String userId =loginResponse.getUserId();	
		System.out.println(loginResponse.getUserId());		
		

		//Add Product		
		RequestSpecification addProductBaseReq=	new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
				.addHeader("authorization", token)
				.build();
		
		RequestSpecification reqAddProduct = given().log().all().spec(addProductBaseReq).param("productName", "Laptop")
				.param("productAddedBy", userId).param("productCategory", "fashion")
				.param("productSubCategory", "shirts").param("productPrice", "11500")
				.param("productDescription", "Lenova").param("productFor", "men")
				.multiPart("productImage",new File("C:\\Users\\ippal\\Downloads\\productImage_1650649434146.jpeg"));

		
		String addProductResponse =reqAddProduct.when().post("/api/ecom/product/add-product").
				then().log().all().extract().response().asString();
		
				JsonPath js = new JsonPath(addProductResponse);
				String productId =js.get("productId");
				
		//Place Order
				
				RequestSpecification createOrderBaseReq=new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
						.addHeader("authorization", token).setContentType(ContentType.JSON).build();
				
				OrderDetails orderdetails=new OrderDetails();
				orderdetails.setCountry("India");				
				orderdetails.setProductOrderedId(productId);
				
				List<OrderDetails> orderList=new ArrayList<>();
				orderList.add(orderdetails);				
				Orders order=new Orders();
				order.setOrders(orderList);
				
				
				RequestSpecification createOrderRes=given().spec(createOrderBaseReq).body(order);
				
				String createOrderResponse =createOrderRes.when().post("/api/ecom/order/create-order")
						.then().log().all().extract().response().asString();
				System.out.print(createOrderResponse);
				
				//Delete Product
				
				RequestSpecification deleteProd=new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
						.addHeader("authorization", token).setContentType(ContentType.JSON).build();
				
				RequestSpecification deleteID=given().spec(deleteProd).pathParam("productId",productId);
				
				String prodDeleteResponse=deleteID.when().delete("api/ecom/product/delete-product/{productId}")
						.then().log().all().extract().response().asString();
				
				JsonPath js1 = new JsonPath(prodDeleteResponse);
				Assert.assertEquals("Product Deleted Successfully",js1.get("message"));

	}

}
