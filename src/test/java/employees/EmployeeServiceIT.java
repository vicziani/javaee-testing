package employees;

import com.arjuna.ats.jta.cdi.TransactionExtension;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import static org.assertj.core.api.Assertions.assertThat;

@EnableAutoWeld
public class EmployeeServiceIT {

    @Inject
    EmployeeService employeeService;

    @WeldSetup
    public WeldInitiator weldInitializator = WeldInitiator.from(WeldInitiator.createWeld()
            .addBeanClasses(EmployeeDao.class, EmployeeService.class)
                    .addExtension(new TransactionExtension())
            )
            .setPersistenceContextFactory(EmployeeServiceIT::createEntityManager)
            .build();

    private static EntityManager createEntityManager(InjectionPoint ip) {
        var factory = Persistence.createEntityManagerFactory("pu");
        return factory.createEntityManager();
    }

    @Test
    void testCreate() {
        employeeService.create("John Doe");
        employeeService.create("Jack Doe");
        var employees = employeeService.findAll();
        assertThat(employees).extracting(Employee::getName)
                .containsExactly("Jack Doe", "John Doe");
    }

    @Test
    void testUpdate() {
        var created = employeeService.create("John Doe");
        employeeService.update(created.getId(), "Jack Doe");
        var employees = employeeService.findAll();
        assertThat(employees).extracting(Employee::getName)
                .containsExactly("Jack Doe");
    }


}
