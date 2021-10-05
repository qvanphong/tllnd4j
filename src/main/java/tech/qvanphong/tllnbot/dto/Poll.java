package tech.qvanphong.tllnbot.dto;

import java.util.ArrayList;
import java.util.List;

public class Poll {
    private String question;
    private List<String> options;
    private StringBuilder optionsStringBuilder = new StringBuilder("");

    public Poll(String question, List<String> options) {
        this.question = question;
        this.options = options;
    }

    public Poll() {
        this.options = new ArrayList<>();
    }

    public void addOption(String option) {
        this.options.add(option);
    }


    public String getOptionsString() {
        return optionsStringBuilder.toString();
    }

    public void appendOptionString(String indexEmoji, String newString) {
        optionsStringBuilder.append(String.format("%s. %s \n", indexEmoji, newString));
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
