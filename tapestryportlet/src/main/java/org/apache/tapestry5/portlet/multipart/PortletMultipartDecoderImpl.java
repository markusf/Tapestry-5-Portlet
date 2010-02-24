// Copyright 2006 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.apache.tapestry5.portlet.multipart;

import static org.apache.tapestry5.ioc.internal.util.CollectionFactory.newMap;

import java.io.File;
import java.util.*;

import javax.portlet.ActionRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.upload.internal.services.UploadedFileItem;
import org.apache.tapestry5.upload.services.UploadSymbols;
import org.apache.tapestry5.upload.services.UploadedFile;

/**
 * @author Raphael Jean
 */
public class PortletMultipartDecoderImpl implements PortletMultipartDecoder
{
	private final Map<String, UploadedFileItem> _uploads = CollectionFactory.newMap();
	
	 private final FileItemFactory _fileItemFactory;
	
    private final String _repositoryLocation;

    private final int _repositoryThreshold;

    private final long _maxRequestSize;

    private final long _maxFileSize;
    
    private FileUploadException uploadException;

    public PortletMultipartDecoderImpl(
            
            FileItemFactory fileItemFactory,
            
            String repositoryLocation, 
            
    		int repositoryThreshold,
    		
    		@Symbol(UploadSymbols.REQUESTSIZE_MAX)
            long maxRequestSize,

            @Symbol(UploadSymbols.FILESIZE_MAX)
            long maxFileSize)
    {
        _repositoryLocation = repositoryLocation;
        _repositoryThreshold = repositoryThreshold;
        _maxRequestSize = maxRequestSize;
        _maxFileSize = maxFileSize;
        _fileItemFactory = fileItemFactory;
    }
    
    public UploadedFile getFileUpload(String parameterName)
    {
        return _uploads.get(parameterName);
    }
    
    public HttpServletRequest decode(HttpServletRequest request) 
    {
        unsupported("decode(HttpServletRequest)");
        
        return null;
    }

    public ActionRequest decode(ActionRequest request)
    {
    	
        List<FileItem> fileItems = parseRequest(request);

        return processFileItems(request, fileItems);
    }
    
    @SuppressWarnings("unchecked")
    protected List<FileItem> parseRequest(ActionRequest request)
    {
        try
        {
            return createFileUpload().parseRequest(request);
        }
        catch (FileUploadException e)
        {
            uploadException = e;

            return Collections.emptyList();
        }
    }
    
    private PortletFileUpload createFileUpload()
    {
      
        PortletFileUpload upload = new PortletFileUpload(_fileItemFactory);

        // set maximum file upload size
        upload.setSizeMax(_maxRequestSize);
        upload.setFileSizeMax(_maxFileSize);

        return upload;
    }
    
    protected ActionRequest processFileItems(ActionRequest request,
            List<FileItem> fileItems)
    {
        if (uploadException == null && (fileItems == null || fileItems.isEmpty())) {
            return request;
        }

        ParametersPortletRequestWrapper wrapper = new ParametersPortletRequestWrapper(request);

        for (FileItem item : fileItems)
        {
            if (item.isFormField())
            {
                wrapper.addParameter(item.getFieldName(), item.getString());
            }
            else
            {
                wrapper.addParameter(item.getFieldName(), item.getName());
                addUploadedFile(item.getFieldName(), new UploadedFileItem(item));
            }
        }

        return wrapper;
    }
    
    protected void addUploadedFile(String name, UploadedFileItem file)
    {
        _uploads.put(name, file);
    }
    
    protected final void unsupported(String methodName)
    {
        throw new UnsupportedOperationException(methodName);
    }
    
    public FileUploadException getUploadException()
    {
        return uploadException;
    }

}
