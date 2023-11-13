package ccm.server.util;

import ccm.server.entity.MetaData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class CollectionUtility {
    //获取两个ArrayList的差集
    public static <T> Collection<T> receiveDefectList(Collection<T> firstArrayList, Collection<T> secondArrayList) {
        Collection<T> resultList = new ArrayList<>();
        if (firstArrayList != null && secondArrayList != null) {
            log.trace("start to receive defect list: first array->" + firstArrayList.size() + " second array->" + secondArrayList.size());
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            LinkedList<T> result = new LinkedList<>(firstArrayList);
            HashSet<T> othHash = new HashSet<>(secondArrayList);
            result.removeIf(othHash::contains);
            resultList = new ArrayList<>(result);
            log.trace("finish to detect list:" + resultList.size() + PerformanceUtility.stop(stopWatch));
        }
        return resultList;
    }

    //获取两个ArrayList的交集
    public static <T> Collection<T> receiveCollectionList(Collection<T> firstArrayList, Collection<T> secondArrayList) {
        Collection<T> resultList = new ArrayList<>();
        if (firstArrayList != null && secondArrayList != null) {
            log.trace("start to receive collection list: first array->" + firstArrayList.size() + " second array->" + secondArrayList.size());
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            LinkedList<T> result = new LinkedList<>(firstArrayList);
            HashSet<T> othHash = new HashSet<>(secondArrayList);
            result.removeIf(s -> !othHash.contains(s));
            resultList = new ArrayList<>(result);
            log.trace("finish to collection list:" + resultList.size() + PerformanceUtility.stop(stopWatch));
        }
        return resultList;
    }

    public static <T> Collection<T> receiveUnionList(Collection<T> firstArrayList, Collection<T> secondArrayList) {
        Collection<T> resultList = new ArrayList<>();
        if (firstArrayList != null && secondArrayList != null) {
            log.trace("start to receive union list: first array->" + firstArrayList.size() + " second array->" + secondArrayList.size());
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Set<T> firstSet = new TreeSet<>(firstArrayList);
            firstSet.addAll(secondArrayList);
            resultList = new ArrayList<>(firstSet);
            log.trace("finish to collection list:" + resultList.size() + PerformanceUtility.stop(stopWatch));
        }
        return resultList;
    }

    public static List<String> toList(Map<?, Map<String, ?>> source) {
        if (CommonUtility.hasValue(source)) {
            List<String> result = new ArrayList<>();
            for (Map.Entry<?, Map<String, ?>> mapEntry : source.entrySet())
                result.addAll(new ArrayList<>(mapEntry.getValue().keySet()));
            return result;
        }
        return null;
    }

    public static List<String> toList(List<Map<String, ?>> source) {
        if (CommonUtility.hasValue(source)) {
            List<String> result = new ArrayList<>();
            for (Map<String, ?> stringMap : source) {
                result.addAll(new ArrayList<>(stringMap.keySet()));
            }
            return result;
        }
        return null;
    }

    public static Map<String, List<Map<String, Object>>> mapByObid(List<Map<String, Object>> items, String key) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        if (CommonUtility.hasValue(items)) {
            StopWatch stopWatch = PerformanceUtility.start();
            for (Map<String, Object> objectMap : items) {
                if (objectMap != null) {
                    Object obid = objectMap.getOrDefault(key, "");
                    if (obid != null && !StringUtils.isEmpty(obid)) {
                        if (result.containsKey(obid.toString())) {
                            List<Map<String, Object>> maps = result.get(obid.toString());
                            maps.add(objectMap);
                            result.replace(obid.toString(), maps);
                        } else {
                            result.put(obid.toString(), new ArrayList<Map<String, Object>>() {{
                                this.add(objectMap);
                            }});
                        }
                    }
                }
            }
            log.trace("convert list " + items.size() + " into map " + result.size() + PerformanceUtility.stop(stopWatch));
        }
        return result;
    }
}
