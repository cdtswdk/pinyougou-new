package com.itcast.controller;

import com.itcast.message.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/queue")
public class QueueController {

    @Autowired
    private MessageSender messageSender;

    @RequestMapping(value = "/send/text")
    public String sendTextMessage(String text) {
        this.messageSender.sendTextMessage(text);
        return "OK";
    }

    @RequestMapping(value = "send/map")
    public String sendMapMessage() {
        this.messageSender.sendMapMessage();
        return "OK";
    }
}
