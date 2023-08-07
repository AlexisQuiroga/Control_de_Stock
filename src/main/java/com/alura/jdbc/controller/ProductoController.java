package com.alura.jdbc.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alura.jdbc.factory.ConnectionFactory;

public class ProductoController {

	public int modificar(String nombre, String descripcion, Integer cantidad,Integer id) throws SQLException  {

		Connection con = new ConnectionFactory().recuperaConexion();
		
		try(con){
		
		final PreparedStatement statement = con.prepareStatement("UPDATE PRODUCTO SET " 
				+ "NOMBRE = ? "
				+ ", DESCRIPCION = ? "
				+ ", CANTIDAD = ? "
				+ " WHERE ID = ?");
		
		try(statement){
		
        statement.setString(1, nombre);
		statement.setString(2, descripcion);
		statement.setInt(3, cantidad);
		statement.setInt(4, id);
		
		
		statement.execute();
		
		
		int updateCount = statement.getUpdateCount();		
		
		return updateCount;}
		}
	}

	public int eliminar(Integer id) throws SQLException  {
		final Connection con = new ConnectionFactory().recuperaConexion();
		
		try(con){
		final PreparedStatement statement = con.prepareStatement("DELETE FROM PRODUCTO WHERE ID = ?" );
		
		try (statement){
		statement.setInt(1, id);
		statement.execute();
		
		return statement.getUpdateCount();
			}	
		}
	}

	public List<Map<String, String>> listar() throws SQLException {
		final Connection con = new ConnectionFactory().recuperaConexion();
		
		try(con){
		
		final PreparedStatement statement = con.prepareStatement("SELECT ID, NOMBRE, DESCRIPCION, CANTIDAD FROM PRODUCTO");
		
		try(statement){
		statement.execute();
		
		ResultSet resultSet = statement.getResultSet();
		
		List<Map<String, String>> resultado = new ArrayList<>();
		
		while(resultSet.next()) {
			Map<String, String> fila = new HashMap<>();
			fila.put("ID", String.valueOf(resultSet.getInt("ID")));
			fila.put("NOMBRE",resultSet.getString("NOMBRE"));
			fila.put("DESCRIPCION",resultSet.getString("DESCRIPCION"));
			fila.put("CANTIDAD", String.valueOf(resultSet.getInt("CANTIDAD")));
			
			
			resultado.add(fila);
		
		};
		
		return  resultado;
		}
		}
	}

    public void guardar(Map<String, String> producto) throws SQLException {
		
    	String nombre = producto.get("NOMBRE");
    	String descripcion = producto.get("DESCRIPCION");
    	Integer cantidad = Integer.valueOf(producto.get("CANTIDAD"));
    	Integer maximoCantidad = 50;
    	
    	
    final Connection con = new ConnectionFactory().recuperaConexion();
    	try(con){
    		con.setAutoCommit(false);}
    	
    	PreparedStatement statement = con.prepareStatement(
    	"INSERT INTO PRODUCTO (nombre, descripcion, cantidad)" 
    	+ "VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
    	
    	try(statement){
    	
    		do {
    			int cantidadParaGuardar = Math.min(cantidad, maximoCantidad);
    	
    			ejecutaRegistro(nombre, descripcion, cantidadParaGuardar, statement);
		
    			cantidad -= maximoCantidad;
		
		
    		}while(cantidad > 0);
    		
		//Esta para guardar los items agregados siempre y cuando sea mayor a cero el numero de cantidad
    	
    	con.commit();
    	System.out.println("COMMIT");
    	
    	} catch (SQLException e) {
    		//Está para que cuando ocurra un error no guarde nada. Es decir
    		// la transaccion o guarda todo o no guarda nada.
    		
    		con.rollback();
    		
    		System.out.println("ROLLBACK");
    	}
    	
    }
    	


	private void ejecutaRegistro(String nombre, String descripcion, Integer cantidad, PreparedStatement statement)
			throws SQLException {
		
		statement.setString(1, nombre);
    	statement.setString(2, descripcion);
		statement.setInt(3, cantidad);
    	
		statement.execute();
    	/* EL try-with-recources se aplica para hacer el codigo aparte de mas corto 
		nos ayuda a cerrar las aperturas que venimos haciendo en vez de poner ".close()" y en algun caso
		 olvidarnos que se debia poner para no correr riesgos. */
		
		
		// JAVA 7v try-with-resources
		
	/*	try(ResultSet resultSet1 = statement.getGeneratedKeys();){
				while(resultSet1.next()) {
						System.out.println(String.format("Fue incertado el producto de ID %d" ,
								resultSet.getInt(1))); */   		

		//JAVA 9v try-with-resources
		
    	final ResultSet resultSet = statement.getGeneratedKeys();
    	
    	try(resultSet){
			while(resultSet.next()) {
					System.out.println(String.format("Fue incertado el producto de ID %d" ,
							resultSet.getInt(1)));
			}	
    	}
	}
}
