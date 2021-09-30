package conexaoTeste;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

public class conexao {
	private Connection con;
			
	private static String url = " "; // "jdbc:postgresql://localhost:5432/";
	private static String user = " "; // "postgres";
	private static String pass = " "; // "34cas)*10";
	
	static Scanner sc = new Scanner(System.in);
	
	public conexao(String databaseName) {
		
		
		
		System.out.println("Digite seu login de conexão com o banco de dados nos respectivos campos ");
		System.out.println("url: ");
		String url = sc.next();
		System.out.println("Usuário: ");
		String user = sc.next();
		System.out.println("Senha: ");
		String pass = sc.next();
		
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url + databaseName, user, pass);
			//System.out.println("Banco conectado com sucesso!");
		} catch (Exception e) {
			throw new Error("Houve um problema ao conectar no banco de dados!");
		}
	}
	
	private void closeConnection() {
		try {
			if (!this.con.isClosed()) {
				this.con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, String> getSizePerDatabase() {
		HashMap<String, String> response = new HashMap<>();
		
		try {
			String sql = "SELECT *, pg_database.datname, pg_size_pretty(pg_database_size(pg_database.datname)) AS size FROM pg_database WHERE datistemplate is False;";
			PreparedStatement pesquisa = con.prepareStatement(sql);
			
			ResultSet result = pesquisa.executeQuery();
			
			while(result.next()) {
				response.put(result.getString("datname"), result.getString("size"));
			}
		} catch (Exception e) {
			System.out.println("Houve um problema ao requisitar o tamanho dos bancos de dados!");
		}
		
		return response;
	}
	
	private HashMap<String, String> getTableSizeFromAllDatabases() {
		HashMap<String, String> response = new HashMap<>();
		
		try {
			String sql = "select table_schema, table_name, pg_relation_size('\"'||table_schema||'\".\"'||table_name||'\"')\r\n"
					+ "from information_schema.tables\r\n"
					+ "where table_schema NOT IN (\r\n"
					+ "	'pg_catalog',\r\n"
					+ "    'information_schema'\r\n"
					+ ")\r\n"
					+ "order by pg_relation_size DESC";
			
			PreparedStatement pesquisa = con.prepareStatement(sql);
			
			ResultSet result = pesquisa.executeQuery();
			
			while(result.next()) {
				response.put(result.getString("table_name"), result.getString("pg_relation_size"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Houve um problema ao requisitar o tamanho das tabelas de todos os banco de dados!");
		}
		
		return response;
	}
	
	private HashMap<String, String> getUpTimeDatabase() {
		HashMap<String, String> response = new HashMap<>();
		
		try {
			String sql = "SELECT date_trunc('second', current_timestamp - pg_postmaster_start_time()), pg_postmaster_start_time() as uptime;";
			
			PreparedStatement pesquisa = con.prepareStatement(sql);
			
			ResultSet result = pesquisa.executeQuery();
			 
			while(result.next()) {
				response.put(result.getString("date_trunc"), result.getString("uptime"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Houve um problema ao requisitar o tamanho das tabelas de todos os banco de dados!");
		}
		
		return response;
	}
	
	
	public static void main(String[] args) {
		conexao con = new conexao("Felipe");
	
		HashMap<String, String> databasesSize = con.getSizePerDatabase();
		HashMap<String, String> upTimeDataBase = con.getUpTimeDatabase();
		
		
		for (String database : databasesSize.keySet()) {
			conexao conx = new conexao(database);
			
			HashMap<String, String> tableSize = conx.getTableSizeFromAllDatabases();
			System.out.println("------- Database: " + database + " -------");
			
			tableSize.entrySet().stream().forEach(e -> {
				System.out.println("Nome: " + e.getKey() + " | tamanho: " + e.getValue());
			});
			
			if (tableSize.isEmpty()) {
				System.out.println("Nenhuma tabela encontrada");
			}

			conx.closeConnection();
		}
		System.out.println("---------------------------------------");
		System.out.println("Tamanho de cada banco: " + databasesSize);
		System.out.println("Tempo ativo do banco: " + upTimeDataBase);
		

		sc.close();
		con.closeConnection();	
	}
}
