package packageCodificacao ; 
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class mainClass extends layout {
	
	private Connection con;
	//private Connection connSqlite;
	static Scanner sc = new Scanner(System.in);

	public mainClass (String databaseName) {

		ArrayList<String> login = new ArrayList<String>();

		String path = "login.txt";

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = br.readLine();

			while (line != null) {
				// System.out.println(line);
				line = br.readLine();
				login.add(line);
			}
		} catch (IOException e) {
			System.out.println("Erro: " + e);
		}

		// System.out.println(login);

		String url = login.get(1);
		String user = login.get(3);
		String pass = login.get(5);
		
		// System.out.println(url + user + pass);

		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(url + databaseName, user, pass);
			//connSqlite = DriverManager.getConnection(SqLite);
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

			while (result.next()) {
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

			while (result.next()) {
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

			while (result.next()) {
				response.put(result.getString("date_trunc"), result.getString("uptime"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Houve um problema ao requisitar o tamanho das tabelas de todos os banco de dados!");
		}

		return response;
	}

	// Métrica: Querys Mais Rápidas do Servidor

	private HashMap<String, String> gettopQuickQuery() {
		HashMap<String, String> response = new HashMap<>();
		
		ArrayList<String> queries = new ArrayList<String>();

		String path = "queries.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line = br.readLine();

            while (line != null) {
                //System.out.println(line);
                line = br.readLine();
                queries.add(line);
            }
        } catch (IOException e) {
            System.out.println("Erro: " + e);
        }
        
		String getquery = queries.get(0);

		try {
			String sql = "SELECT (total_exec_time / 1000 / 60) as total_minutes, query FROM pg_stat_statements ORDER BY  (total_exec_time / 1000 / 60) asc LIMIT " + getquery;

			PreparedStatement pesquisa = con.prepareStatement(sql);

			ResultSet result = pesquisa.executeQuery();

			while (result.next()) {
				response.put(result.getString("total_minutes"), result.getString("query"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
					"Houve um problema ao requisitar as queries com menor tempo de execução de todos os banco de dados!");
		}

		return response;
	}

	// Métrica: Querys Mais Lentas do Servidor

	private HashMap<String, String> getTopSlowestQueries() {
		HashMap<String, String> response = new HashMap<>();
		
		ArrayList<String> queries = new ArrayList<String>();
		
		String path = "queries.txt";
		
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            String line = br.readLine();

            while (line != null) {
                //System.out.println(line);
                line = br.readLine();
                queries.add(line);
            }
        } catch (IOException e) {
            System.out.println("Erro: " + e);
        }
        
		String getquery = queries.get(1);

		try {
			String sql = "SELECT (total_exec_time / 1000 / 60) as total_minutes, query FROM pg_stat_statements ORDER BY  (total_exec_time / 1000 / 60) desc LIMIT " + getquery;

			PreparedStatement pesquisa = con.prepareStatement(sql);

			ResultSet result = pesquisa.executeQuery();

			while (result.next()) {
				response.put(result.getString("total_minutes"), result.getString("query"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
					"Houve um problema ao requisitar as queries com maior tempo de execução de todos os banco de dados!");
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

			while (result.next()) {
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

			while (result.next()) {
				response.put(result.getString("datname"), result.getString("deadlocks"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Houve um problema ao requisitar número de deadLocks do banco");
		}

		return response;

	}

	// Métrica: Queries Que Mais Consomem Espaço Temporário no Servidor

	private HashMap<String, String> gettopConsumersTemporarySpace() {
		HashMap<String, String> response = new HashMap<>();

		try {
			String sql = "select userid::regrole, query from pg_stat_statements order by temp_blks_written desc limit 10;";

			PreparedStatement pesquisa = con.prepareStatement(sql);

			ResultSet result = pesquisa.executeQuery();

			while (result.next()) {
				response.put(result.getString("userid"), result.getString("query"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
					"Houve um problema ao requisitar as queries que mais consomem espaço temporário no servidor");
		}

		return response;
	}

	// Métrica: Otimização de Operações de Entrada/Saída no Servidor

	private HashMap<String, String> gettopIOIntensiveQueries() {
		HashMap<String, String> response = new HashMap<>();

		try {
			String sql = "select userid::regrole, dbid, query\r\n" + "    from pg_stat_statements\r\n"
					+ "    order by (blk_read_time+blk_write_time)/calls desc\r\n" + "    limit 10;";

			PreparedStatement pesquisa = con.prepareStatement(sql);

			ResultSet result = pesquisa.executeQuery();

			while (result.next()) {
				response.put(result.getString("userid"), result.getString("query"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Houve um problema ao requisitar a otimização de operações de Entrada/Saída");
		}

		return response;
	}
	
	// INTERFACE
	
	public static void main(String[] args) throws SQLException, IOException  {
				
		mainClass  con = new mainClass ("postgres");
		String SqLite = "jdbc:sqlite:historico.db";
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Connection connSqlite = DriverManager.getConnection(SqLite);
		 Statement statement = connSqlite.createStatement();
		 statement.setQueryTimeout(30);	
		 
		 File[] diskPartition = File.listRoots();
	        
	        SimpleDateFormat formatter= new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm:ss");
			Date date = new Date(System.currentTimeMillis());
	        
	        System.out.println("\n\n=====================================================================================\n");
			
			System.err.println(""+formatter.format(date)+"\n");

	        if (diskPartition != null && diskPartition.length > 0) {
	            for (File aDrive : diskPartition) {
	   
	         System.out.println("Espaço Total no Disco ("+ aDrive+") : " + aDrive.getTotalSpace() / (1024*1024*1024) + " GB");
	         System.out.println("Espaço Usado: "+ (aDrive.getTotalSpace() - aDrive.getUsableSpace()) / (1024 *1024*1024) + " GB");
	         System.out.println("Espaço Livre: "+ aDrive.getFreeSpace() / (1024 *1024*1024) + " GB\n");
	        
	            }
	      }
        
		System.out.println("\n=====================================================================================\n");
		
		layout st = new layout();
			
		// Interfaces Fora do Laço de Repetição
		
		HashMap<String, String> databasesSize = con.getSizePerDatabase(); 
		HashMap<String, String> upTimeDataBase = con.getUpTimeDatabase();
		HashMap<String, String> queryConnection = con.getQueryConnection();
		HashMap<String, String> getdeadlocksNumber = con.getDeadlocksNumber();
		HashMap<String, String> topQuickQuery = con.gettopQuickQuery();
		HashMap<String, String> TopSlowestQueries = con.getTopSlowestQueries(); 
		HashMap<String, String> topConsumersTemporarySpace = con.gettopConsumersTemporarySpace();
		HashMap<String, String> topIOIntensiveQueries = con.gettopIOIntensiveQueries();
	
		// Laço de Repetição

		for (String database : databasesSize.keySet()) {
		
			mainClass  conx = new mainClass (database);
		
			// Métrica: Nome do Banco
			
			HashMap<String, String> tableSize = conx.getTableSizeFromAllDatabases();
			
			System.out.println("\n\n------- Database: " + database + " -------" + "\n");
			
			// Métrica: Nome e Tamanho da Tabela
			
			tableSize.entrySet().stream().forEach(e -> {
				//System.out.println("TABELA: " + e.getKey() + " | TAMANHO: " + e.getValue() + "\r");
	
				try {
					//statement.executeUpdate("create table tableSize('data_horas','banco','Nome','tamanho')");
					statement.executeUpdate("insert into tableSize values('"+formatter.format(date)+"','"+database+"','"+e.getKey()+"','"+e.getValue()+"')");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
				
				st.setShowVerticalLines(true);
		        st.setHeaders("TABELA", "TAMANHO");
		        st.addRow(e.getKey(), e.getValue());				
			});
			
			System.err.println(formatter.format(date)+"\n");

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
				
				System.err.println(formatter.format(date)+"\n");

				System.out.println("\n------- Status geral do backend -------\n\n");
				
				queryConnection.entrySet().stream().forEach(e -> {
					//System.out.println("Banco: " + e.getKey() + " | Status: " + e.getValue());
					
					try {
						//statement.executeUpdate("create table queryconnection('data_horas','banco','status')");
						statement.executeUpdate("insert into queryconnection('data_horas','banco','status') values('"+formatter.format(date)+"','"+e.getKey()+"', '"+e.getValue()+"')");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					st.setShowVerticalLines(true);
			        st.setHeaders("BANCO", "STATUS");
			        st.addRow(e.getKey(), e.getValue());
				});
				
				st.print();
				st.clean();
				
				// Métrica: Número de DeadLocks do Banco
				
				System.out.println("\n\n=====================================================================================\n");
				
				System.err.println(formatter.format(date)+"\n");

				System.out.println("\n------- Número de DeadLocks -------\n\n");
				
				getdeadlocksNumber.entrySet().stream().forEach(e -> {
					//System.out.println("Número de DeadLocks: " + e.getValue() + " | Banco: " + e.getKey());
					
					try {
						//statement.executeUpdate("create table deadlocks('data_horas','num_deadlocks','banco')");
						statement.executeUpdate("insert into deadlocks('data_horas','num_deadlocks','banco') values('"+formatter.format(date)+"','"+e.getKey()+"', '"+e.getValue()+"')");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					st.setShowVerticalLines(true);
			        st.setHeaders("BANCO", "DEADLOCKS");
			        st.addRow(e.getKey(), e.getValue());
				});
				
				st.print();
				st.clean();
				
				// Métrica: Tamanho, Data e Hora de Criação do Banco / Servidor
				
				System.out.println("\n\n=====================================================================================\n");
				
				System.err.println(formatter.format(date)+"\n");
				
				System.out.println("------- Tamanho dos Bancos Existentes: -------\n");
				
				databasesSize.entrySet().stream().forEach(e -> {
					System.out.println("BANCO: " + e.getKey() + " | TAMANHO: " + e.getValue());
					System.out.println("  ");
					
					try {
						//statement.executeUpdate("create table databasesSize('data_horas','nome','tamanho')");
						statement.executeUpdate("insert into databasesSize('data_horas','nome','tamanho') values('"+formatter.format(date)+"','"+e.getKey()+"', '"+e.getValue()+"')");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				});
				
				System.out.println("\n\n------- Hora de Criaão do Servidor: -------\n");

				upTimeDataBase.entrySet().stream().forEach(e -> {
					System.out.println("User: " + e.getKey() + " | Query: " + e.getValue());
					System.out.println("  ");
					try {
						//statement.executeUpdate("create table upTimeDataBase('data_horas','date_trunc','uptime')");
						statement.executeUpdate("insert into upTimeDataBase('data_horas','date_trunc','uptime') values('"+formatter.format(date)+"','"+e.getKey()+"', '"+e.getValue()+"')");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});

				//System.out.println("Tempo ativo do banco: " + upTimeDataBase);
				
				// Métrica: Queries Que Mais Consomem Espaço Temporário no Servidor
				System.out.println("\n\n=====================================================================================\n");
				
				System.err.println(formatter.format(date)+"\n");

				System.out.println("------- Queries que Mais Consomem Espaço Temporário no Servidor -------\n\n");
				
				topConsumersTemporarySpace.entrySet().stream().forEach(e -> {
					System.out.println("User: " + e.getKey() + " | Query: " + e.getValue());
					System.out.println("  ");
					try {
						//statement.executeUpdate("create table top10temporary_space('data_horas','user','query')");
						statement.executeUpdate("insert into top10temporary_space('data_horas','user','query') values('"+formatter.format(date)+"','"+e.getKey()+"', '"+e.getValue()+"')");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				});
				
				// Métrica: Otimização de Operações de Entrada/Saída no Servidor
				
				System.out.println("\n\n=====================================================================================\n");
				
				System.err.println(formatter.format(date)+"\n");

	            System.out.println("------- Otimização de Operações de Entrada/Saída no Servidor -------\n\n");
	            
	            topIOIntensiveQueries.entrySet().stream().forEach(e -> {
	                System.out.println("User id: " + e.getKey() + " | Query: " + e.getValue());               
	                System.out.println("  ");
	               try {
	            	   	//statement.executeUpdate("create table IntensiveQueries('data_horas','user id','query')");
						statement.executeUpdate("insert into IntensiveQueries('data_horas','user id','query') values('"+formatter.format(date)+"','"+e.getKey()+"', '"+e.getValue()+"')");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
	            });
				
				// Métrica Rápidas
				
				System.out.println("\n\n\n\n=====================================================================================\n");
				
				System.err.println(formatter.format(date)+"\n");

				System.out.println("------- Tempo de execução das queries mais rápidas -------\n\n");

				topQuickQuery.entrySet().stream().forEach(e -> {
					
					System.out.println("Velocidade: " + e.getKey() + " | Query: " + e.getValue());
					System.out.println("\n___________________________________________________________________________________________________________________________________________________________________________________________________________\n");
					System.out.println("  ");
					try {
						//statement.executeUpdate("create table QuickQueries('data_horas','velocidade','query')");
						statement.executeUpdate("insert into QuickQueries('data_horas','velocidade','query') values('"+formatter.format(date)+"','"+e.getKey()+"', '"+e.getValue()+"')");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
					
					
				// Métrica Lentas
				
				System.out.println("\n\n=====================================================================================\n");
				
				System.err.println(formatter.format(date)+"\n");

				System.out.println("------- Tempo de execução das queries mais lentas -------\n\n");

				TopSlowestQueries.entrySet().stream().forEach(e -> {
					
					System.out.println("Velocidade: " + e.getKey() + " | Query: " + e.getValue());
					System.out.println("\n___________________________________________________________________________________________________________________________________________________________________________________________________________\n");
					System.out.println("  ");
					try {
						//statement.executeUpdate("create table LowestQueries('data_horas','velocidade','query')");
						statement.executeUpdate("insert into LowestQueries('data_horas','velocidade','query') values('"+formatter.format(date)+"','"+e.getKey()+"', '"+e.getValue()+"')");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
	
				sc.close();
				
				con.closeConnection();	
	}
}