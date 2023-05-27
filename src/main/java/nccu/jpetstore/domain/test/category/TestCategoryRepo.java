package nccu.jpetstore.domain.test.category;

import nccu.jpetstore.domain.entity.category.EventSourcedCategoryRepository;
import nccu.jpetstore.domain.core.EventStore;
import nccu.jpetstore.domain.entity.category.Category;

public class TestCategoryRepo {

    private final static EventStore eventStore = new EventStore(
            "esdb://127.0.0.1:2113?tls=false&keepAliveTimeout=10000&keepAliveInterval=10000");
    private final static EventSourcedCategoryRepository repository = new EventSourcedCategoryRepository(eventStore);

    public static void main(String[] args){
        testFindBy();
//        testCreate();
        testAppend();
    }

    private static void testAppend() {
        Category category = repository.findBy("3235999e-cc9d-45c2-b99c-587ab8a99037");
        category.setName("Dog-updated-1");
        category.setDescription("This is a dog category-updated-1");
        System.out.println(repository.save(category));
    }

    private static void testFindBy() {
        Category category = repository.findBy("ab93a881-9931-41ff-a11d-6456d4ff1795");
        System.out.println(category);
    }

    private static void testCreate() {
        Category category = new Category();
        category.setName("Dog");
        category.setDescription("This is a dog category");
        System.out.println(repository.save(category));
    }


}
