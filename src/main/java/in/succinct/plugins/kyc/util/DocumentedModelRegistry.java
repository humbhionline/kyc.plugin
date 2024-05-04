package in.succinct.plugins.kyc.util;

import com.venky.cache.Cache;
import com.venky.swf.db.model.Model;
import in.succinct.plugins.kyc.db.model.DocumentedModel;

import java.util.HashSet;
import java.util.Set;

public class DocumentedModelRegistry {
    private static volatile DocumentedModelRegistry sSoleInstance;

    //private constructor.
    private DocumentedModelRegistry() {
        //Prevent form the reflection api.
        if (sSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static DocumentedModelRegistry getInstance() {
        if (sSoleInstance == null) { //if there is no instance available... create new one
            synchronized (DocumentedModelRegistry.class) {
                if (sSoleInstance == null) sSoleInstance = new DocumentedModelRegistry();
            }
        }

        return sSoleInstance;
    }

    //Make singleton from serialize and deserialize operation.
    protected DocumentedModelRegistry readResolve() {
        return getInstance();
    }

    Cache<String,Class<?>> cache = new Cache<String, Class<?>>() {
        @Override
        protected  Class<?> getValue(String s) {
            try {
                return Class.forName(s);
            }catch (Exception ex){
                return null;
            }
        }
    };

    public Set<String> documentModelNames(){
        return new HashSet<>(cache.keySet());
    }
    public <M extends  Model & DocumentedModel> void register(Class<M> clazz){
        register(clazz.getSimpleName(),clazz);
    }
    public <M extends  Model & DocumentedModel> void register(String documentedModelName, Class<M> clazz){
        cache.put(documentedModelName,clazz);
    }

    @SuppressWarnings("unchecked")
    public <M extends Model & DocumentedModel> Class<M> getDocumentedModelClass(String documentedModelName){
        return (Class<M>)cache.get(documentedModelName);
    }
}
