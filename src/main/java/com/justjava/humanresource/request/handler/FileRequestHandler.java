package com.justjava.humanresource.request.handler;
import com.justjava.humanresource.request.dto.CreateWorkflowRequestCommand;
import com.justjava.humanresource.request.entity.*;
import com.justjava.humanresource.request.enums.RequestType;
import com.justjava.humanresource.request.repository.FileRequestDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor public class FileRequestHandler implements WorkflowRequestTypeHandler {
 private final FileRequestDetailRepository repository;
 public RequestType supportedType(){return RequestType.FILE_REQUEST;}
 public void validate(CreateWorkflowRequestCommand c){if(c.getFileRequest()==null) throw new IllegalArgumentException("File request details are required.");}
 public void saveDetails(WorkflowRequest r,CreateWorkflowRequestCommand c){var p=c.getFileRequest(); FileRequestDetail d=new FileRequestDetail(); d.setWorkflowRequestId(r.getId()); applyPayload(d,p); repository.save(d);}
 public void updateDetails(WorkflowRequest r,CreateWorkflowRequestCommand c){var p=c.getFileRequest(); FileRequestDetail d=repository.findByWorkflowRequestId(r.getId()).orElseGet(FileRequestDetail::new); if(d.getWorkflowRequestId()==null){d.setWorkflowRequestId(r.getId());} applyPayload(d,p); repository.save(d);}
 private void applyPayload(FileRequestDetail d, CreateWorkflowRequestCommand.FileRequestPayload p){ d.setFileCategory(p.getFileCategory()); d.setConfidentialityLevel(p.getConfidentialityLevel()); d.setRequestedAccessType(p.getRequestedAccessType()); d.setRetentionRequired(p.isRetentionRequired()); d.setPurpose(p.getPurpose()); }
}