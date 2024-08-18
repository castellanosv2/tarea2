package persona.;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tu_paquete.model.Persona;
import com.tu_paquete.service.PersonaService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImpl implements PersonaService {

    private final String DATA_FILE = "personas.json";
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Persona> obtenerPersonas() {
        return leerDatos();
    }

    @Override
    public Persona obtenerPersonaPorId(int id) {
        return leerDatos().stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    @Override
    public Persona crearPersona(Persona persona) {
        List<Persona> personas = leerDatos();
        persona.setId(personas.isEmpty() ? 1 : personas.get(personas.size() - 1).getId() + 1);
        personas.add(persona);
        guardarDatos(personas);
        return persona;
    }

    @Override
    public Persona actualizarPersona(int id, Persona persona) {
        List<Persona> personas = leerDatos();
        Optional<Persona> personaExistente = personas.stream().filter(p -> p.getId() == id).findFirst();
        if (personaExistente.isPresent()) {
            Persona p = personaExistente.get();
            p.setNombre(persona.getNombre());
            p.setEdad(persona.getEdad());
            p.setCorreo(persona.getCorreo());
            guardarDatos(personas);
            return p;
        }
        return null;
    }

    @Override
    public void eliminarPersona(int id) {
        List<Persona> personas = leerDatos();
        personas.removeIf(p -> p.getId() == id);
        guardarDatos(personas);
    }

    private List<Persona> leerDatos() {
        try {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                file.createNewFile();
                return List.of();
            }
            return objectMapper.readValue(file, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de datos", e);
        }
    }

    private void guardarDatos(List<Persona> personas) {
        try {
            objectMapper.writeValue(new File(DATA_FILE), personas);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo de datos", e);
        }
    }
}
