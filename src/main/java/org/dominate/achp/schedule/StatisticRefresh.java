package org.dominate.achp.schedule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwja.tool.utils.LoadUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dominate.achp.common.cache.StatisticCache;
import org.dominate.achp.common.enums.SceneCountType;
import org.dominate.achp.entity.ChatScene;
import org.dominate.achp.entity.dto.StatisticSceneDTO;
import org.dominate.achp.service.IChatSceneService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class StatisticRefresh {

    private final IChatSceneService chatSceneService;

    @Scheduled(cron = "0 30 * * * ?")
    public void saveSceneCount() {
        if (!LoadUtil.onProd()) {
            return;
        }
        Map<Integer, StatisticSceneDTO> statisticSceneMap = new HashMap<>();
        parseSceneCount(statisticSceneMap, SceneCountType.SEND);
        parseSceneCount(statisticSceneMap, SceneCountType.CHAT);
        parseSceneCount(statisticSceneMap, SceneCountType.READ);
        QueryWrapper<ChatScene> query = new QueryWrapper<>();
        query.lambda().in(ChatScene::getId, statisticSceneMap.keySet())
                .select(ChatScene::getId, ChatScene::getChatCount, ChatScene::getSendCount, ChatScene::getReadCount);
        List<ChatScene> sceneList = chatSceneService.list(query);
        List<ChatScene> updateSceneList = new ArrayList<>(statisticSceneMap.size());
        for (ChatScene scene : sceneList) {
            StatisticSceneDTO statistic = statisticSceneMap.get(scene.getId());
            ChatScene update = new ChatScene();
            update.setId(scene.getId());
            update.setReadCount(scene.getReadCount() + statistic.getAddReadCount());
            update.setChatCount(scene.getChatCount() + statistic.getAddChatCount());
            update.setSendCount(scene.getSendCount() + statistic.getAddSendCount());
            updateSceneList.add(update);
        }
        chatSceneService.updateBatchById(updateSceneList);
    }


    private static void parseSceneCount(Map<Integer, StatisticSceneDTO> statisticSceneMap, SceneCountType countType) {
        List<Integer> sceneIdList = StatisticCache.popCountSceneList(countType);
        for (Integer sceneId : sceneIdList) {
            if (!statisticSceneMap.containsKey(sceneId)) {
                StatisticSceneDTO statisticScene = new StatisticSceneDTO(sceneId);
                statisticSceneMap.put(sceneId, statisticScene);
            }
            switch (countType) {
                case CHAT:
                    statisticSceneMap.get(sceneId).addChatCount();
                    break;
                case SEND:
                    statisticSceneMap.get(sceneId).addSendCount();
                    break;
                case READ:
                    statisticSceneMap.get(sceneId).addReadCount();
                    break;
                default:
                    break;
            }
        }
    }
}
