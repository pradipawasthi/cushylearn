package com.pradip.cushylearn.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rbaisak on 3/16/17.
 */

public class ModuleModel {

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }

  private String topicName;
  private String chapterName;
  private String question;
  private String answer;
  private String others;
  private String url;
  private String subjectId;


  private String AttachmentType;

  public String getAttachmentType() {
    return AttachmentType;
  }

  public void setAttachmentType(String attachmentType) {
    AttachmentType = attachmentType;
  }


  public String getSubjectId() {
    return subjectId;
  }

  public void setSubjectId(String subjectId) {
    this.subjectId = subjectId;
  }




  public String getTopiclistId() {
    return topiclistId;
  }

  public void setTopiclistId(String topiclistId) {
    this.topiclistId = topiclistId;
  }

  private String topiclistId;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }







  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getOthers() {
    return others;
  }

  public void setOthers(String others) {
    this.others = others;
  }



  public String getChapterName() {
    return chapterName;
  }

  public void setChapterName(String chapterName) {
    this.chapterName = chapterName;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }



  @Exclude
  public Map<String, Object> toMap() {
    Map<String, Object> result = new HashMap<>();
    result.put("topicName", topicName);
   // result.put("name", name);
    result.put("question", question);
//        result.put("address", address);
    result.put("AttachmentType", AttachmentType);
    result.put("subjectId", subjectId);
    result.put("topiclistId", topiclistId);


    result.put("answer", answer);
    result.put("others", others);

    result.put("url", url);
    result.put("chapterName;", chapterName);


    return result;
  }

}