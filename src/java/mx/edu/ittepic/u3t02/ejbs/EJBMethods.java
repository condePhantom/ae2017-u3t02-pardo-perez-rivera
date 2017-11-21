/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.edu.ittepic.u3t02.ejbs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.persistence.EntityExistsException;
//import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import mx.edu.ittepic.u3t02.entities.Role;
import mx.edu.ittepic.u3t02.utils.Message;

/**
 *
 * @author kon_n
 */
@Stateless
@Path("/roles")
public class EJBMethods {
    @PersistenceContext
    private EntityManager em;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @GET    
    @Produces({MediaType.APPLICATION_JSON})
    public String roles() {    
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Message m = new Message();

        Query q; //hacer consulta
        List<Role> listRoles; //guardar el resultado
        q = em.createNamedQuery("Role.findAll");//consulta como si hay una que nos trae todo los roles utilizamos el Role.findAll si no esta se tiene que crear
        try {
            listRoles = q.getResultList();
            m.setCode(HttpServletResponse.SC_OK);
            m.setMessage("La consulta se ejecutó correctamente.");
            m.setDetail(gson.toJson(listRoles));
            //return gson.toJson(listRoles);
        } catch (IllegalStateException | QueryTimeoutException | TransactionRequiredException | PessimisticLockException | LockTimeoutException e) {
            m.setCode(HttpServletResponse.SC_BAD_REQUEST);
            m.setMessage("No se pudieron obtener los roles, intente nuevamente.");
            m.setDetail(e.toString());
        } catch (PersistenceException e) {
            m.setCode(HttpServletResponse.SC_BAD_REQUEST);
            m.setMessage("No se pudieron obtener los roles, intente nuevamente.");
            m.setDetail(e.toString());
        }
        return gson.toJson(m);
    }
    @GET
    @Path("/{id}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON})
    public String rolesId(@PathParam("id") String id) {
        Query q;
        Role rol;
        Message m = new Message();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try{
            int n= Integer.parseInt(id);
            q = em.createNamedQuery("Role.findByRoleid").setParameter("roleid", n);
            try {
                rol = (Role) q.getSingleResult();
                m.setCode(HttpServletResponse.SC_OK);
                m.setMessage("La consulta se ejecutó correctamente.");
                m.setDetail(gson.toJson(rol));
            } catch (NoResultException e) {
                m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                m.setMessage("No se encontraron resultados.");
                m.setDetail(e.toString());
            } catch (NonUniqueResultException e) {
                m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                m.setMessage("Existe más de un resultado.");
                m.setDetail(e.toString());
            } catch (IllegalStateException | QueryTimeoutException | TransactionRequiredException | PessimisticLockException | LockTimeoutException e) {
                m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                m.setMessage("No se pudo obtener el rol, intente nuevamente.");
                m.setDetail(e.toString());
            } catch (PersistenceException e) {
                m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                m.setMessage("No se pudo obtener el rol, intente nuevamente.");
                m.setDetail(e.toString());
            }
        }catch(NumberFormatException z){
            m.setCode(HttpServletResponse.SC_BAD_REQUEST);
            m.setMessage("Formato incorrecto, intente nuevamente.");
            m.setDetail(z.toString());
        }
        return gson.toJson(m);
    }
    @PUT
    @Path("/eliminar/{id}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteRole(@PathParam("id") String id){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Message m = new Message();
        Role rol;
        try {
            int n= Integer.parseInt(id);
            try{
                rol = em.find(Role.class, n);
                if (rol == null) {
                    m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                    m.setMessage("No se pudo eliminar el rol, verifique que sea correcto e intente nuevamente.");
                    m.setDetail("El id proporcionado no está asociado con ningún rol.");
                } else {
                    //if (rol.getUsersList().isEmpty()) {
                        try {
                            em.remove(rol);
                            m.setCode(HttpServletResponse.SC_OK);
                            m.setMessage("El rol " + rol.getRoleid() + " se eliminó correctamente.");
                            m.setDetail(gson.toJson(rol));
                        } catch (IllegalArgumentException | PersistenceException e) {
                            m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                            m.setMessage("No se pudo eliminar el rol, verifique que sea correcto e intente nuevamente.");
                            m.setDetail(e.toString());
                        }
                    /*} else {
                        m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                        m.setMessage("No se pudo eliminar el rol, verifique que sea correcto e intente nuevamente.");
                        m.setDetail("El rol tiene usuarios asociados, no es posible eliminarlo.");
                    }*/
                }
            } catch (IllegalArgumentException e) {
                m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                m.setMessage("No se pudo eliminar el rol, verifique que sea correcto e intente nuevamente.");
                m.setDetail(e.toString());
            }
        }catch(NumberFormatException z){
            m.setCode(HttpServletResponse.SC_BAD_REQUEST);
            m.setMessage("Formato incorrecto, intente nuevamente.");
            m.setDetail(z.toString());
        }
        return gson.toJson(m);
    }

    
    @POST
    @Path("/crear")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String createRole(JsonObject param) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Message m = new Message();
        
        Role rol = new Role();
        String s= param.getString("rolename");
        rol.setRolename(s);
        try {
            //Crear nuevo registro en la BD.
            em.persist(rol);
            //Obligar al contenedor a guardar en la BD.
            em.flush();
            m.setCode(HttpServletResponse.SC_OK);
            m.setMessage("El rol se creó correctamente con la clave " + rol.getRoleid());
            m.setDetail(gson.toJson(rol));
        } catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e) {
            m.setCode(HttpServletResponse.SC_BAD_REQUEST);
            m.setMessage("No se pudo guardar el rol, intente nuevamente.");
            m.setDetail(e.toString());
        }
        return gson.toJson(m);
    }
    @PUT
    @Path("/actualizar/{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String updateRole(@PathParam("id") String id,JsonObject param){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Message m = new Message();
        Role rol;
        try {
            int n= Integer.parseInt(id);
            try{
                rol = em.find(Role.class, n);
                if (rol == null) {
                    m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                    m.setMessage("No se pudo actualizar el rol, verifique que sea correcto e intente nuevamente.");
                    m.setDetail("El id proporcionado no está asociado con ningún rol.");
                } else {
                    rol.setRolename(param.getString("rolename"));
                    try {
                        em.merge(rol);
                        em.flush();
                        m.setCode(HttpServletResponse.SC_OK);
                        m.setMessage("El rol " + rol.getRoleid() + " se actualizó correctamente.");
                        m.setDetail(gson.toJson(rol));
                    } catch (IllegalArgumentException | PersistenceException e) {
                        m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                        m.setMessage("No se pudo actualizar el rol, verifique que sea correcto e intente nuevamente.");
                        m.setDetail(e.toString());
                    }
                }
            } catch (IllegalArgumentException e) {
                m.setCode(HttpServletResponse.SC_BAD_REQUEST);
                m.setMessage("No se pudo actualizar el rol, verifique que sea correcto e intente nuevamente.");
                m.setDetail(e.toString());
            }
        }catch(NumberFormatException e){
            m.setCode(HttpServletResponse.SC_BAD_REQUEST);
            m.setMessage("Formato incorrecto, intente nuevamente.");
            m.setDetail(e.toString());
        }
        return gson.toJson(m);
    }
}
