package com.example.application.data.generator;

import com.example.application.data.entity.Company;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Gender;
import com.example.application.data.entity.Status;
import com.example.application.data.repository.CompanyRepository;
import com.example.application.data.repository.ContactRepository;
import com.example.application.data.repository.StatusRepository;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.internal.Pair;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringComponent
public class DataGenerator {

    
    List <Pair<String,Gender>> listOfPair = new ArrayList<>();

    Gender female = Gender.Female;
    Gender male = Gender.Male;
    public String[] firstName = {"Ece","Melis","Ela","Ayşe","Mehmet","Buğra","Efe","Burak","Ebru","Ecem"};;
    public String[] lastName = {"Aytürk","Yılmaz","Karahan","Sağlam","Güneş","Toprak","Kılıç","Şimşek","Uslu","Yeşil"};
    
    
   
    @Bean
    public CommandLineRunner loadData(ContactRepository contactRepository, CompanyRepository companyRepository,
            StatusRepository statusRepository) {
       

        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (contactRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");
            ExampleDataGenerator<Company> companyGenerator = new ExampleDataGenerator<>(Company.class,
                    LocalDateTime.now());
            companyGenerator.setData(Company::setName, DataType.COMPANY_NAME);
            List<Company> companies = companyRepository.saveAll(companyGenerator.create(5, seed));

            List<Status> statuses = statusRepository
                    .saveAll(Stream.of("Imported lead", "Not contacted", "Contacted", "Customer", "Closed (lost)")
                            .map(Status::new).collect(Collectors.toList()));
          

            logger.info("... generating 50 Contact entities...");
            ExampleDataGenerator<Contact> contactGenerator = new ExampleDataGenerator<>(Contact.class,
                    LocalDateTime.now());
            
         
            contactGenerator.setData(Contact::setEmail, DataType.EMAIL);

            Random r = new Random(seed);
            List<Contact> contacts = contactGenerator.create(100, seed).stream().map(contact -> {

                contact.setCompany(companies.get(r.nextInt(companies.size())));
                contact.setStatus(statuses.get(r.nextInt(statuses.size())));
                contact.setIdentityNumber(generateIdentityNumber());
               
                
                return contact;
            }).collect(Collectors.toList());

            String[][] names = new String[100][2];
            int counter = 0;
            
            for(int i = 0; i < firstName.length; i++){
                for(int j = 0; j < lastName.length; j++){
                if(firstName[i].matches("Ece|Melis|Ela|Ayşe|Ebru|Ecem")){
                    listOfPair.add(new Pair<String,Gender> (firstName[i],female));
                }
                else{
                    listOfPair.add(new Pair<String,Gender> (firstName[i],male));
                }
                  names[counter][0] = firstName[i];
                  names[counter][1] = lastName[j];
                  counter++;
                }           
            }
            for(int i = 0; i < 100; i++){
                contacts.get(i).setFirstName(listOfPair.get(i).getFirst());
                contacts.get(i).setLastName(names[i][1]);
                contacts.get(i).setGender(listOfPair.get(i).getSecond().toString());
            }
            
           // foreach(String ad :Arrays.asList(firstName)){
             //foreach(String soyad :Arrays.asList(lastName)){
       // }
        //}

            
            contactRepository.saveAll(contacts);
            
            logger.info("Generated demo data");
        };
    }

    private String generateIdentityNumber() {

        int a1 = getRandomInteger(1,9);
        int a2 = getRandomInteger(0,9);
        int a3 = getRandomInteger(0,9);
        int a4 = getRandomInteger(0,9);
        int a5 = getRandomInteger(0,9);
        int a6 = getRandomInteger(0,9);
        int a7 = getRandomInteger(0,9);
        int a8 = getRandomInteger(0,9);
        int a9 = getRandomInteger(0,9);
        
        int a10 =((((a1+a3+a5+a7+a9)*7)+(a2+a4+a6+a8)*9)%10);
        int a11 =((a1+a2+a3+a4+a5+a6+a7+a8+a9+a10)%10);
  
    String id = (a1+""+a2+""+a3+""+a4+""+a5+""+a6+""+a7+""+a8+""+a9+""+a10+""+a11);
    return id;
        
        
    }
    public static int getRandomInteger(int maximum, int minimum){ 
        return ((int) (Math.random()*(maximum - minimum))) + minimum; 
    }
    
   
 


  
}
