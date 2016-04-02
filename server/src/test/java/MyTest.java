import com.mongodb.DB;
import com.zxd.task.cache.JedisClient;
import com.zxd.task.model.Region;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

/**
 * Created by zxd on 15/6/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-server-context.xml")
public class MyTest {

    @Autowired
    private JedisClient jedisClient;
    @Autowired
    private MongoTemplate mongoTemplate;
    private static int flag = 0;

    @Test
    public void test() {
        Object obj = jedisClient.getAllKeys("*");
        Object obj1 = jedisClient.getAllHkeys("lottery_rate_config");
        System.out.println(obj.toString());
    }

    @Test
    public void _test() {
        Set<String> colls = this.mongoTemplate.getCollectionNames();
        for (String coll : colls) {
            log.info("--------------CollectionName=" + coll);
        }
        DB db = this.mongoTemplate.getDb();
        log.info("--------------db=" + db.toString());
    }

    @Test
    public void createCollection() {
        if (!this.mongoTemplate.collectionExists(Region.class)) {
            this.mongoTemplate.createCollection(Region.class);
        }
    }

    @Test
    public void findListByAge() {
        int id = 1;
        Query query = new Query();
        query.addCriteria(new Criteria("id").is(id));
        List<Region> list = this.mongoTemplate.find(query, Region.class);
        for(Region e : list){
            System.out.println("--------------" + e.toString());
        }
    }

    @Test
    public void findOne() {
        Query query = new Query();
        int id = 1;
        query.addCriteria(new Criteria("id").is(id));
        Region region = this.mongoTemplate.findOne(query, Region.class);
        System.out.println("--------------" + region.toString());
    }

    @Test
    public void  findOneByUsername() {
        String regionName = "hz";
        Query query = new Query();
        query.addCriteria(new Criteria("regionName").is(regionName));
        Region region =  this.mongoTemplate.findOne(query, Region.class);
        System.out.println("--------------" + region.toString());
    }

    @Test
    public void insert() {
        Region entity = new Region();
        entity.setId(1);
        entity.setRegionName("hz");
        this.mongoTemplate.insert(entity);
    }

    @Test
    public void update() {
        Region entity = new Region();
        entity.setId(1);
        entity.setRegionName("sjz");
        Query query = new Query();
        query.addCriteria(new Criteria("id").is(entity.getId()));
        Update update = new Update();
        update.set("id", entity.getId());
        update.set("regionName", entity.getRegionName());
        this.mongoTemplate.updateFirst(query, update, Region.class);
    }

}
