package com.pinyougou.search.service.mq;

import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.List;

public class TopicMessageListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        if(message instanceof ObjectMessage){
            //强转成objectMessage类型
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                //获取消息内容，强转成MessageInfo
                MessageInfo messageInfo = (MessageInfo) objectMessage.getObject();
                //如果时修改，则增加索引
                if(messageInfo.getMethod() == MessageInfo.METHOD_UPDATE){
                    List<Item> items = (List<Item>) messageInfo.getContext();
                    this.itemSearchService.importItems(items);
                }else if(messageInfo.getMethod() == MessageInfo.METHOD_DELETE){
                    //如果是删除，则删除索引
                    List<Long> ids = (List<Long>) messageInfo.getContext();
                    this.itemSearchService.deleteByGoodsIds(ids);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
