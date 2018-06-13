package com.zxd.task.timego;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 自动拆分
 * @author zxd <hzzhangxiaodan@corp.netease.com>
 * @since 18/3/16.
 */
public class SplitTool {

    public static void main(String[] args) {
        BuModel znkSecond= new BuModel("纸尿裤", 3);
        BuModel ymSecond= new BuModel("孕妈", 1);
        ymSecond.setMin(1);
        Map<String, List<BuModel>> configMap = Maps.newHashMap();
        configMap.put("纸尿裤", Lists.newArrayList(znkSecond, ymSecond));

        BuModel nfSecond= new BuModel("奶粉", 3);
        BuModel fsSecond= new BuModel("辅食/营养品/零食", 1);
        fsSecond.setMin(1);
        configMap.put("奶粉", Lists.newArrayList(nfSecond, fsSecond));

        BuModel tztxSecond= new BuModel("童装童鞋", 2);
        BuModel wjtsSecond= new BuModel("玩具图书", 1);
        BuModel xhwySecond= new BuModel("洗护喂养", 1);
        BuModel jjcxSecond= new BuModel("家居出行", 1);
        wjtsSecond.setMin(1);
        configMap.put("母婴其他", Lists.newArrayList(tztxSecond, wjtsSecond, xhwySecond, jjcxSecond));


        ResultModel resultModel0 = new ResultModel("2018/3/16", "0", "纸尿裤", 4);
        ResultModel resultModel7 = new ResultModel("2018/3/16", "7", "纸尿裤", 5);
        ResultModel resultModel9 = new ResultModel("2018/3/16", "9", "纸尿裤", 3);
        ResultModel resultModel13 = new ResultModel("2018/3/16", "13", "纸尿裤", 2);
        ResultModel resultModel17 = new ResultModel("2018/3/16", "17", "纸尿裤", 6);
        ResultModel resultModel21 = new ResultModel("2018/3/16", "21", "纸尿裤", 7);
        ResultModel resultModeln0 = new ResultModel("2018/3/16", "0", "奶粉", 4);
        ResultModel resultModeln7 = new ResultModel("2018/3/16", "7", "奶粉", 5);
        ResultModel resultModeln9 = new ResultModel("2018/3/16", "9", "奶粉", 3);
        ResultModel resultModeln13 = new ResultModel("2018/3/16", "13", "奶粉", 2);
        ResultModel resultModeln17 = new ResultModel("2018/3/16", "17", "奶粉", 6);
        ResultModel resultModeln21 = new ResultModel("2018/3/16", "21", "奶粉", 7);
        ResultModel resultModelq0 = new ResultModel("2018/3/16", "0", "母婴其他", 4);
        ResultModel resultModelq7 = new ResultModel("2018/3/16", "7", "母婴其他", 5);
        ResultModel resultModelq9 = new ResultModel("2018/3/16", "9", "母婴其他", 3);
        ResultModel resultModelq13 = new ResultModel("2018/3/16", "13", "母婴其他", 2);
        ResultModel resultModelq17 = new ResultModel("2018/3/16", "17", "母婴其他", 6);
        ResultModel resultModelq21 = new ResultModel("2018/3/16", "21", "母婴其他", 7);


        List<ResultModel> requestList = Lists.newArrayList(resultModel0,
                resultModel7,resultModel9,resultModel13,resultModel17,resultModel21, resultModeln0,
                resultModeln7,resultModeln9,resultModeln13,resultModeln17,resultModeln21, resultModelq0,
                resultModelq7,resultModelq9,resultModelq13,resultModelq17,resultModelq21);
        Map<String, List<BuModel>> resultMap = Maps.newHashMap();
        for (ResultModel req : requestList) {
            List<BuModel> buModelList = configMap.get(req.getFirstBu());
            int totalWeight = buModelList.stream().mapToInt(BuModel::getWeight).sum();
            int leftWeight = req.getFirstBuCount();
            List<BuModel> ret = Lists.newArrayList();
            for (BuModel buModel : buModelList){
                if (leftWeight <= 0) {
                    if (buModel.getMin() > 0) {
                        BuModel tempRet = new BuModel(buModel);
                        tempRet.setNumber(tempRet.getMin());
                        leftWeight -= tempRet.getMin();
                        ret.add(tempRet);
                    }
                    break;
                }
                int number = BigDecimal.valueOf(req.getFirstBuCount()).multiply(BigDecimal.valueOf(buModel.getWeight())).divide
                        (BigDecimal.valueOf(totalWeight), 0, BigDecimal.ROUND_HALF_UP).toBigInteger()
                        .intValue();
                BuModel tempRet = new BuModel(buModel);
                tempRet.setNumber(number);
                ret.add(tempRet);
                leftWeight = leftWeight - number;
            }
            if (leftWeight != 0) {
                for (BuModel buModel : ret) {
                    if (leftWeight > 0 || buModel.getNumber() >= -leftWeight) {
                        buModel.setNumber(buModel.getNumber() + leftWeight);
                        break;
                    } else {
                        leftWeight += buModel.getNumber();
                        buModel.setNumber(0);
                    }
                    if (leftWeight == 0) {
                        break;
                    }
                }
            }
            resultMap.put(req.getDate() + req.getSchedule() + req.getFirstBu(), ret);
            System.out.println("");
        }
        System.out.println("");

        for (ResultModel req : requestList) {
            List<BuModel> ret = resultMap.get(req.getDate() + req.getSchedule() + req.getFirstBu());
            for (BuModel bu : ret) {
                req.setSecondBu(bu.getName());
                for (int i = 0; i < bu.getNumber(); i++) {
                    System.out.println(req.toString());
                }
            }
        }
    }
}
