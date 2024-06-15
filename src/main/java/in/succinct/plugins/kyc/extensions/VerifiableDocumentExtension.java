package in.succinct.plugins.kyc.extensions;

import com.venky.core.io.ByteArrayInputStream;
import com.venky.core.string.StringUtil;
import com.venky.core.util.ObjectUtil;
import com.venky.swf.db.extensions.ModelOperationExtension;
import com.venky.swf.db.model.Model;
import com.venky.swf.exceptions.AccessDeniedException;
import com.venky.swf.routing.Config;
import in.succinct.plugins.kyc.db.model.VerifiableDocument;
import org.owasp.encoder.Encode;

import javax.activation.MimeType;
import javax.activation.MimetypesFileTypeMap;
import java.net.URL;

public class VerifiableDocumentExtension<M extends VerifiableDocument & Model> extends VerifiableExtension<M> {
    @Override
    public void beforeValidate(M document) {
        super.beforeValidate(document);
        if (document.getImageUrl() != null && !document.getImageUrl().startsWith(Config.instance().getServerBaseUrl())){
            try {
                URL url = new URL(Encode.forUri(document.getImageUrl()));
                ByteArrayInputStream inputStream = new ByteArrayInputStream(StringUtil.readBytes(url.openStream()));
                document.setFile(inputStream);
                if (!ObjectUtil.isVoid(document.getFileContentName())){
                    document.setFileContentType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(document.getFileContentName()));
                }
            }catch (Exception ex){
                //
            }

        }
        if (document.getRawRecord().isFieldDirty("FILE") ){
            try {
                if (document.getFile() != null) {
                    document.setFileContentSize(document.getFile().available());
                }else {
                    document.setFileContentSize(0);
                }
            }catch (Exception ex){
                //
            }
        }
    }
}
