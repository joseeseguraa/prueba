package tds.gestiongastos.adapters.repository;

import java.util.List;
import tds.gestiongastos.modelo.Notificacion;

public interface RepositorioNotificaciones {
    
    List<Notificacion> getAllNotificaciones();
    
    void addNotificacion(Notificacion notificacion);
}