package packageCodificacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class mainClass extends layout {
	
	private Connection con;
	
	static Scanner sc = new Scanner(System.in);
	
	public mainClass(String databaseName) {
		
	    /* private static String url = sc.next(); // "  jdbc:postgresql://localhost:5432/ ";
	       private static String user = sc.next(); // "postgres";
	    private static String pass = sc.next(); // "phantom02s"; */

	    	ArrayList<String> login = new ArrayList<String>();

	        String path = "C:\\eclipse-workspace\\apiNectoSystem\\login.txt";

	        try (BufferedReader br = new BufferedReader(new FileReader(path))){
	            String line = br.readLine();

	            while (line != null) {
	                //System.out.println(line);
	                line = br.readLine();
	                login.add(line);
	            }
	        } catch (IOException e) {
	            System.out.println("Erro: " + e);
	        }

	        //System.out.println(login);

	        String url = login.get(0);
	        String user = login.get(1);
	        String pass = login.get(2);

	        //System.out.println(url + user + pass);
		
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
	
	// MÉTRICAS
	
	// Métrica: Nome e Tamanho do Banco
	
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
	
	// Métrica: Nome e Tamanho da Tabela
	
	private HashMap<String, String> getTableSizeFromAllDatabases() {
		HashMap<String, String> response = new HashMap<>();
		
		try {
			String sql = "select table_schema, table_name, pg_relation_size('\"'||table_schema||'\".\"'||table_name||'\"')\r\n"
					+ "from information_schema.tables\r\n" + "where table_schema NOT IN (\r\n" + "	'pg_catalog',\r\n"
					+ "    'information_schema'\r\n" + ")\r\n" + "order by pg_relation_size DESC";
			
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
	
	// Métrica: Data e Hora de Criação do Banco
	
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
	
	// Métrica: 5 Querys Mais Rápidas do Servidor
	
	private HashMap<String, String> getTop5QuickQuery() {
		HashMap<String, String> response = new HashMap<>();
		
		try {
			String sql = "SELECT (total_exec_time / 1000 / 60) as total_minutes, query FROM pg_stat_statements ORDER BY  (total_exec_time / 1000 / 60) asc LIMIT 5";
			
			PreparedStatement pesquisa = con.prepareStatement(sql);
			
			ResultSet result = pesquisa.executeQuery();
			 
			while(result.next()) {
				response.put(result.getString("total_minutes"), result.getString("query"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Houve um problema ao requisitar as queries com menor tempo de execução de todos os banco de dados!");
		}
		
		return response;
	}
	
	// Métrica: 5 Querys Mais Lentas do Servidor
	
	private HashMap<String, String> getTop5SlowestQueries() {
		HashMap<String, String> response = new HashMap<>();
		
		try {
			String sql = "SELECT (total_exec_time / 1000 / 60) as total_minutes, query FROM pg_stat_statements ORDER BY  (total_exec_time / 1000 / 60) desc LIMIT 5";
			
			PreparedStatement pesquisa = con.prepareStatement(sql);
			
			ResultSet result = pesquisa.executeQuery();
			 
			while(result.next()) {
				response.put(result.getString("total_minutes"), result.getString("query"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Houve um problema ao requisitar as queries com maior tempo de execução de todos os banco de dados!");
		}
		
		return response;
	}
	
	// Métrica: Status Geral do Backend
	
	private HashMap<String, String> getQueryConnection() {
		HashMap<String, String> response = new HashMap<>();
		
		try {
			String sql = "SELECT datname, state from pg_stat_activity WHERE datname is not null;";
			
			PreparedStatement pesquisa = con.prepareStatement(sql);
			
			ResultSet result = pesquisa.executeQuery();
			 
			while(result.next()) {
				response.put(result.getString("datname"), result.getString("state"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Houve um problema ao requisitar status geral do backend");
		}
		
		return response;
	}
	
	// Métrica: Número de DeadLocks do Banco 
	
		private HashMap<String, String> getDeadlocksNumber() {
			HashMap<String, String> response = new HashMap<>();
			
			try {
				String sql = "SELECT datname, deadlocks from pg_stat_database where datname is not null;";
				
				PreparedStatement pesquisa = con.prepareStatement(sql);
				
				ResultSet result = pesquisa.executeQuery();
				 
				while(result.next()) {
					response.put(result.getString("datname"), result.getString("deadlocks"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Houve um problema ao requisitar número de deadLocks do banco");
			}
			
			return response;
		}
	
	// Métrica: Queries Que Mais Consomem Espaço Temporário no Servidor
	
		private HashMap<String, String> getTop10ConsumersTemporarySpace() {
			HashMap<String, String> response = new HashMap<>();
			
			try {
				String sql = "select userid::regrole, query from pg_stat_statements order by temp_blks_written desc limit 10;";
				
				PreparedStatement pesquisa = con.prepareStatement(sql);
				
				ResultSet result = pesquisa.executeQuery();
				 
				while(result.next()) {
					response.put(result.getString("userid"), result.getString("query"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Houve um problema ao requisitar as queries que mais consomem espaço temporário no servidor");
			}
			
			return response;
		}
			
	// Métrica: Otimização de Operações de Entrada/Saída no Servidor

		private HashMap<String, String> getTop10IOIntensiveQueries() {
	        HashMap<String, String> response = new HashMap<>();

	        try {
	            String sql = "select userid::regrole, dbid, query\r\n" + 
	                    "    from pg_stat_statements\r\n" + 
	                    "    order by (blk_read_time+blk_write_time)/calls desc\r\n" + 
	                    "    limit 10;";

	            PreparedStatement pesquisa = con.prepareStatement(sql);

	            ResultSet result = pesquisa.executeQuery();

	            while(result.next()) {
	                response.put(result.getString("userid"), result.getString("query"));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Houve um problema ao requisitar a otimização de operações de Entrada/Saída");
	        }

	        return response;
	    }
		
	// CSV
		
		// Criação dos CSVs
		
		public static void Writecsv_dbaSize(HashMap<String, String> databasesSize) {
			
		    try (PrintWriter writer = new PrintWriter(new File("csv\\DatabaseSize.csv"))) {

		        StringBuilder sb = new StringBuilder();
		        for (String database: databasesSize.keySet()) {
		        sb.append(database);
		        sb.append(';');
		        }
		        sb.append('\n');
		        for (String tamanho: databasesSize.values()) {
		        sb.append(tamanho);
		        sb.append(';');


		        }
		        writer.write(sb.toString());
		        writer.close();



		    } catch (FileNotFoundException e) {
		        System.out.println(e.getMessage());

		    }
		}
		
		public static void Writecsv(String chave, String valor) {
				 
			try (PrintWriter writer = new PrintWriter(new File("csv\\tablesize.csv"))) {

				StringBuilder sb = new StringBuilder();

				sb.append(chave);
				sb.append(';');
				sb.append(valor);
				sb.append('\n');

				writer.write(sb.toString());
				writer.close();

				

			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());

			}
		}

		public static void Writecsv_dbaUptime(HashMap<String, String> upTimeDataBase) {
			 
			try (PrintWriter writer = new PrintWriter(new File("csv\\uptime.csv"))) {
	
				StringBuilder sb = new StringBuilder();
				for (String database: upTimeDataBase.keySet()) {
				sb.append(database);
				sb.append(';');
				}
				sb.append('\n');
				for (String tempo : upTimeDataBase.values()) {
				sb.append(tempo);
				sb.append(';');
			
				
				}
				writer.write(sb.toString());
				writer.close();
	
				
	
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
	
			}
		}
		
		public static void Writecsv_dbdeadlocks(HashMap<String, String> getdeadlocksNumber) {
			 
			try (PrintWriter writer = new PrintWriter(new File("csv\\deadlocks.csv"))) {
	
				StringBuilder sb = new StringBuilder();
				for (String numero: getdeadlocksNumber.keySet()) {
				sb.append(numero);
				sb.append(';');
				}
				sb.append('\n');
				for (String database : getdeadlocksNumber.values()) {
				
				sb.append(database);
				sb.append(';');
				
				
				}
				writer.write(sb.toString());
				writer.close();
	
				
	
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
	
			}
		}
		
		public static void Writecsv_dbalowest(HashMap<String, String> top5SlowestQueries) {
			 
			try (PrintWriter writer = new PrintWriter(new File("csv\\lowest.csv"))) {
	
				StringBuilder sb = new StringBuilder();
				for (String velocidade: top5SlowestQueries.keySet()) {
				sb.append(velocidade);
				sb.append(';');
				}
				sb.append('\n');
				for (String queries : top5SlowestQueries.values()) {
				sb.append(queries);
				sb.append(';');
			
				
				}
				writer.write(sb.toString());
				writer.close();
	
				
	
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
	
			}
		}

		public static void Writecsv_dbaTemporarySpace(HashMap<String, String> top10ConsumersTemporarySpace) {
				 
			try (PrintWriter writer = new PrintWriter(new File("csv\\temporaryspace.csv"))) {
	
				StringBuilder sb = new StringBuilder();
				for (String user: top10ConsumersTemporarySpace.keySet()) {
				sb.append(user);
				sb.append(';');
				
				}
				sb.append('\n');
				for (String queries : top10ConsumersTemporarySpace.values()) {
				
				sb.append(queries);
				sb.append(';');
			
				
				}
				writer.write(sb.toString());
				writer.close();
	
				
	
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
	
			}
		}

		public static void Writecsv_QCon(HashMap<String, String> queryConnection) {
	
			try (PrintWriter writer = new PrintWriter(new File("csv\\QConnection.csv"))) {
	
				StringBuilder sb = new StringBuilder();
				for (String banco : queryConnection.keySet()) {
					sb.append(banco);
					sb.append(';');
				}
				sb.append('\n');
				
				for (String status : queryConnection.values()) {
					sb.append(status);
					sb.append(';');
					
				}
				writer.write(sb.toString());
				writer.close();
	
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
	
			}
		}

		public static void Writecsv_QFast(HashMap<String, String> top5QuickQuery) {
	
			try (PrintWriter writer = new PrintWriter(new File("csv\\quickquery.csv"))) {
	
				StringBuilder sb = new StringBuilder();
				for (String velocidade :top5QuickQuery.keySet()) {
					sb.append(velocidade);
					sb.append(';');
					
				}
				sb.append('\n');
				for (String query : top5QuickQuery.values()) {
					sb.append(query);
					sb.append(';');
	
				}
				writer.write(sb.toString());
				writer.close();
	
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
	
			}
		}

		public static void Writecsv_dbaIQ(HashMap<String, String> top10IOIntensiveQueries) {
	
			try (PrintWriter writer = new PrintWriter(new File("csv\\intensiveQueries.csv"))) {
	
				StringBuilder sb = new StringBuilder();
				for (String user_id :top10IOIntensiveQueries.keySet()) {
					sb.append(user_id);
					sb.append(';');
				}
				sb.append('\n');
				for (String query : top10IOIntensiveQueries.values()) {
					sb.append(query);
					sb.append(';');
	
				}
				writer.write(sb.toString());
				writer.close();
	
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
	
			}
		}
			
	// INTERFACE
	
	public static void main(String[] args) {
		
		mainClass con = new mainClass("postgres");
		
		layout st = new layout();
	
		// Interfaces Fora do Laço de Repetição
		
		HashMap<String, String> databasesSize = con.getSizePerDatabase();
		HashMap<String, String> upTimeDataBase = con.getUpTimeDatabase();
		HashMap<String, String> queryConnection = con.getQueryConnection();
		HashMap<String, String> getdeadlocksNumber = con.getDeadlocksNumber();
		HashMap<String, String> top5QuickQuery = con.getTop5QuickQuery();
		HashMap<String, String> top5SlowestQueries = con.getTop5SlowestQueries();
		HashMap<String, String> top10ConsumersTemporarySpace = con.getTop10ConsumersTemporarySpace();
		HashMap<String, String> top10IOIntensiveQueries = con.getTop10IOIntensiveQueries();
		
		// Laço de Repetição

		for (String database : databasesSize.keySet()) {
			
			mainClass conx = new mainClass(database);
		
			// Métrica: Nome do Banco
			
			HashMap<String, String> tableSize = conx.getTableSizeFromAllDatabases();
			System.out.println("\n\n------- Database: " + database + " -------" + "\n");
			Writecsv_dbaSize(databasesSize);
				
			// Métrica: Nome e Tamanho da Tabela
			
			tableSize.entrySet().stream().forEach(e -> {
				//System.out.println("Nome: " + e.getKey() + " | tamanho: " + e.getValue() + "\r");
				
				st.setShowVerticalLines(true);
		        st.setHeaders("TABELA", "TAMANHO");
		        st.addRow(e.getKey(), e.getValue());
		        Writecsv(e.getKey(),e.getValue());
			});
			
			st.print();
			st.clean();
			
			if (tableSize.isEmpty()) {
				System.out.println("Nenhuma tabela encontrada");
			}
				
			conx.closeConnection();
			
		}
		
			// Interfaces Fora do Laço de Repetição
				
				// Métrica: Status Geral do Backend
				
				System.out.println("\n\n=====================================================================================\n");
				System.out.println("\n------- Status geral do backend -------\n\n");
				
				queryConnection.entrySet().stream().forEach(e -> {
					//System.out.println("Banco: " + e.getKey() + " | Status: " + e.getValue());
					
					st.setShowVerticalLines(true);
			        st.setHeaders("BANCO", "STATUS");
			        st.addRow(e.getKey(), e.getValue());
			        Writecsv_QCon(queryConnection);
			
				});
				
				st.print();
				st.clean();
				
				// Métrica: Número de DeadLocks do Banco
				
				System.out.println("\n\n=====================================================================================\n");
				System.out.println("\n------- Número de DeadLocks -------\n\n");
				
				getdeadlocksNumber.entrySet().stream().forEach(e -> {
					//System.out.println("Número de DeadLocks: " + e.getValue() + " | Banco: " + e.getKey());
					
					st.setShowVerticalLines(true);
			        st.setHeaders("BANCO", "DEADLOCKS");
			        st.addRow(e.getKey(), e.getValue());
					Writecsv_dbdeadlocks(getdeadlocksNumber);

				});
				
				st.print();
				st.clean();
				
				// Métrica: Data e Hora de Criação do Banco
				
				System.out.println("\n\n=====================================================================================\n\n");
				System.out.println("Tamanho de cada banco: " + databasesSize + "\n");
				System.out.println("Tempo ativo do banco: " + upTimeDataBase);
				Writecsv_dbaUptime(upTimeDataBase);
				
				// Métrica: Queries Que Mais Consomem Espaço Temporário no Servidor
				
				System.out.println("\n\n=====================================================================================\n");
				System.out.println("\n------- Queries que Mais Consomem Espaço Temporário no Servidor -------\n\n");

				top10ConsumersTemporarySpace.entrySet().stream().forEach(e -> {
					System.out.println("User: " + e.getKey() + " | Query: " + e.getValue());
					System.out.println("  ");
					Writecsv_dbaTemporarySpace(top10ConsumersTemporarySpace);
					
				});
				
				// Métrica: Otimização de Operações de Entrada/Saída no Servidor
				
				System.out.println("\n\n=====================================================================================\n");
	            System.out.println("\n------- Otimização de Operações de Entrada/Saída no Servidor -------\n\n");
	            
	            top10IOIntensiveQueries.entrySet().stream().forEach(e -> {
	                System.out.println("User id: " + e.getKey() + " | Query: " + e.getValue());
	                System.out.println("  ");
	                Writecsv_dbaIQ(top10IOIntensiveQueries);

	            });
				
				// Métrica Lenta
				
				System.out.println("\n\n=====================================================================================\n");
				System.out.println("\n------- Tempo de execução das 5 queries mais rápidas -------\n\n");

				top5QuickQuery.entrySet().stream().forEach(e -> {
					System.out.println("Velocidade: " + e.getKey() + " | Query: " + e.getValue());
					System.out.println("\n____________________________________________________________________________________________________________________________________________________________________________________________\n");
					System.out.println("  ");
					Writecsv_QFast(top5QuickQuery);

				});
					
				// Métrica Rápida
				
				System.out.println("\n\n=====================================================================================\n");
				System.out.println("\n------- Tempo de execução das 5 queries mais lentas -------\n\n");

				top5SlowestQueries.entrySet().stream().forEach(e -> {
					System.out.println("Velocidade: " + e.getKey() + " | Query: " + e.getValue());
					System.out.println("\n____________________________________________________________________________________________________________________________________________________________________________________________\n");
					System.out.println("  ");
					Writecsv_dbalowest(top5SlowestQueries);

				});
	
				con.closeConnection();	
				
				System.out.println("  ");
	}
}