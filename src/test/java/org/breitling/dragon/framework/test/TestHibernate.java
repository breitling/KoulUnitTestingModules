package org.breitling.dragon.framework.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import javax.persistence.EntityManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.breitling.dragon.framework.types.TestWithHibernate;
import org.breitling.dragon.framework.utils.Person;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context.xml"})
@Category(org.breitling.dragon.framework.category.BasicTests.class)
public class TestHibernate extends TestWithHibernate
{
    @BeforeClass
    public static void testSetUp()
    {
        TestHibernate.testSetup("test", "classpath:/person.xml");
    }
    
    @Before
    public void testCaseSetup()
    {
        super.initializeDBSchemaForClass(Person.class);
        super.testCaseSetup();
    }
    
    @Test
    public void testGetEntityManager_GetManager_NotNull()
    {
        assertNotNull(TestHibernate.getEntityManager());
    }
    
    @Test
    public void testPersist_Person_hasId()
    {
        Person person = new Person("Ana");
        EntityManager em = TestHibernate.getEntityManager();
        
        em.getTransaction().begin();
        em.persist(person);
        em.getTransaction().commit();
        
        assertNotNull(person.getId());
        assertTrue(person.getId() > 0);
        
        int id = person.getId();
        
        Person p = (Person) em.find(Person.class, id);
        
        assertEquals("Ana", p.getFirstName());
        assertEquals(id, (int) p.getId());
    }
   
    @Test
    public void testFind_APerson_Object()
    {
        Person p = (Person) TestHibernate.getEntityManager().find(Person.class, 99);
        
        assertNotNull(p);
        assertEquals("Bob", p.getFirstName());
    }
    
    @Test
    public void testDBConnection_Select_Object()
    {
        Person person = new Person("Jo");
        EntityManager em = TestHibernate.getEntityManager();
        
        em.getTransaction().begin();
        em.persist(person);
        em.getTransaction().commit();
        
        assertEquals(person.getId(), this.getInt("SELECT id FROM person WHERE firstName = 'Jo'"));
    }
}
