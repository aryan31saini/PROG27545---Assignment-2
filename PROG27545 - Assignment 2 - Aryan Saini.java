import java.util.ArrayList; 
import java.util.List; 
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence; 
import javax.persistence.Query; 
import javax.servlet.http.HttpSession; 
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.ModelAttribute; 
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.servlet.mvc.support.RedirectAttributes;  @Controller public class PetController 
 {   
private List<Pet> pets;       
private boolean editMode;      
private int editId;edited    
private EntityManagerFactory emfactory;   
private EntityManager entitymanager;        
public PetController()    
{      
 pets = new ArrayList<>();       editMode = false;       
editId = 0; 
   }        
@ModelAttribute    public void addAttributes(Model model)   
{       model.addAttribute("pet", new Pet());       model.addAttribute("pets", pets);       
model.addAttribute("editMode", editMode);       
model.addAttribute("editId", editId);    }        @GetMapping("/list")    
public String listPets(Model model)    {       return "list";    }        @GetMapping("/add")    
public String showAddForm(Model model)    
{       return "add";    }        @PostMapping("/add")    
public String addPet(@ModelAttribute Pet pet, RedirectAttributes redirect)    {       
	if(editMode)       {          
		for(Pet p : pets)          {             
			if(p.getId() == editId)            
			{                
				p.setName(pet.getName());                
				p.setKind(pet.getKind());                
				p.setGender(pet.getGender());                
				p.setVaccination(pet.isVaccination());                
				editMode = false;                
				editId = 0;                
				break;            
				}         
			}      
		}       else       
		{          
			pet.setId(pets.size() + 1);          
			pets.add(pet);      
			
		}              redirect.addFlashAttribute("message", "Pet added/updated successfully!");      
		return "redirect:/list";    
		}        @GetMapping("/edit")    
		public String showEditForm(@RequestParam int id, RedirectAttributes redirect)   
		{       editMode = true;       
		editId = id;       redirect.addFlashAttribute("message", "Edit mode enabled!");       
		return "redirect:/list";    }        @GetMapping("/delete")    
		public String deletePet(@RequestParam int id, RedirectAttributes redirect)   
		{       for(Pet p : pets)       
		{          if(p.getId() == id)          
		{             pets.remove(p);             
		break;          
		}       }       redirect.addFlashAttribute("message", "Pet deleted successfully!");      
		return "redirect:/list";    }        @GetMapping("/search")    
		public String searchPets(@RequestParam String keyword, Model model)    
		{       List<Pet> result = new ArrayList<>();       
		for(Pet p : pets)       {          if(p.getName().contains(keyword))          {             result.add(p);          }       }       model.addAttribute("pets", result);       return "list";    }        @GetMapping("/logout")    public String logout(HttpSession session)    {       session.invalidate();       return "redirect:/login";    }        @GetMapping("/login")    public String login()    {       return "login";    }        @PostMapping("/login")    public String login(@RequestParam String username, @RequestParam String password,          HttpSession session, RedirectAttributes redirect)    {       if(username.equals("admin") && password.equals("admin"))       {          session.setAttribute("username", username);          session.setMaxInactiveInterval(60*5);          redirect.addFlashAttribute("message", "Logged in successfully!");          return "redirect:/list";       }       redirect.addFlashAttribute("message", "Invalid username or password!");       return "redirect:/login";    }        @GetMapping("/view")    public String viewPets(@RequestParam int id)    {       return "view";    }        @GetMapping("/database")    public String viewDatabase()    {       return "database";    }        @GetMapping("/viewdb")    public String viewPetsDB(@RequestParam int id)    {       return "viewdb";    }        @PostMapping("/viewdbupdate")    public String updatePetsDB(@ModelAttribute Pet pet, RedirectAttributes redirect)    {       entitymanager = emfactory.createEntityManager();       entitymanager.getTransaction().begin();       Pet p = entitymanager.find(Pet.class, pet.getId());              p.setName(pet.getName());       p.setKind(pet.getKind());       p.setGender(pet.getGender());       p.setVaccination(pet.isVaccination());       entitymanager.getTransaction().commit();       entitymanager.close();              redirect.addFlashAttribute("message", "Pet updated successfully!");       return "redirect:/database";    }        @GetMapping("/viewdbdelete")    public String deletePetsDB(@RequestParam int id, RedirectAttributes redirect)    {       entitymanager = emfactory.createEntityManager();       entitymanager.getTransaction().begin();       Query query = entitymanager.createQuery("DELETE Pet p WHERE p.id = :id");       query.setParameter("id", id);       query.executeUpdate();       entitymanager.getTransaction().commit();       entitymanager.close();              redirect.addFlashAttribute("message", "Pet deleted successfully!");       
return "redirect:/database";    }        @PostMapping("/database")    
public String addPetsDB(@ModelAttribute Pet pet, RedirectAttributes redirect)    {       entitymanager = emfactory.createEntityManager();       entitymanager.getTransaction().begin();       entitymanager.persist(pet);       entitymanager.getTransaction().commit();       entitymanager.close();              redirect.addFlashAttribute("message", "Pet added successfully!");       return "redirect:/database";    }        @GetMapping("/databasesearch")    public String searchPetsDB(@RequestParam String keyword, Model model)    {       entitymanager = emfactory.createEntityManager();       entitymanager.getTransaction().begin();       Query query = entitymanager.createQuery("SELECT p FROM Pet p WHERE p.name LIKE :keyword");       query.setParameter("keyword", "%" + keyword + "%");       List<Pet> result = query.getResultList();       entitymanager.getTransaction().commit();       entitymanager.close();              model.addAttribute("pets", result);       return "list";    }        @GetMapping("/databaseview")    public String viewPetsDB()    {       return "databaseview";    }        @PostMapping("/databaseview")    public String addPetsDB(@RequestParam int id, Model model)    {       entitymanager = emfactory.createEntityManager();       entitymanager.getTransaction().begin();       Pet pet = entitymanager.find(Pet.class, id);       entitymanager.getTransaction().commit();       entitymanager.close();              model.addAttribute("pet", pet);              return "databaseview";    }        @PostConstruct    public void init()    {       emfactory = Persistence.createEntityManagerFactory( "Eclipselink_JPA" );    }        @PreDestroy    public void destroy()    {       emfactory.close();    } }