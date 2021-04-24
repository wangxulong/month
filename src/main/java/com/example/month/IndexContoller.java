package com.example.month;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

/**
 * 描述
 *
 * @author wangxulong
 * @version 20210424
 */
@Controller
@Slf4j
public class IndexContoller {

    @GetMapping
    public String index(){
        return "index";
    }
    @PostMapping("calc")
    @ResponseBody
    public Map<String,Object> calc(@RequestBody Form form){
        final LocalDate begin = LocalDate.parse(form.getBegin());
        final LocalDate end = LocalDate.parse(form.getEnd());
        log.info("form:{},",form);

        String result = "";
        if(begin.isAfter(end)){
            return Map.of("result","开始时间不能小于结束时间");
        }

        if(isSameMonth(begin,end)){
            int beginDays = getEndWithDays(begin);
            int endDays = getEndWithDays(end);
            log.info("同一个月,计费天数：{}天", endDays - beginDays);
            result = String.format("同一个月,计费天数：%s 天",  endDays-beginDays +1);
            return Map.of("result",result);

        }else{

            final int beginMonthDays = getBeginWithDays(begin);
            final int endMonthDays = getEndWithDays(end);
            final int month = getRealMonthRange(begin,end);
            log.info("开始月记录天数：{}",beginMonthDays);
            log.info("中间月份：{}",month);
            log.info("结束月月记录天数：{}",endMonthDays);

            result = String.format("<h6>开始月- 计费天数：%d </h6><h6> 中间- 月数：%d </h6><h6>结束月-计费天数：%d </h6>", beginMonthDays,month,endMonthDays);
            return Map.of("result",result);

        }

    }


    public boolean isSameMonth(LocalDate begin,LocalDate end){
        final LocalDate beginFirstDay = begin.with(TemporalAdjusters.firstDayOfMonth());
        final LocalDate endFirstDay = end.with(TemporalAdjusters.firstDayOfMonth());
        return beginFirstDay.isEqual(endFirstDay);

    }

    public int getRealMonthRange(LocalDate begin,LocalDate end){
        LocalDate realLastDayOfMonth = begin.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        int month = 0;
        if(!isSameMonth(realLastDayOfMonth,end)){
            final LocalDate newBegin = begin.with(TemporalAdjusters.firstDayOfMonth()).plusMonths(1);
            final LocalDate newEnd = end.with(TemporalAdjusters.firstDayOfMonth());
            log.info("newBegin:{},newEnd:{}",newBegin,newEnd);
            month = Period.between(newBegin,newEnd).getMonths();
        }
        return month;
    }
    public int getBeginWithDays(LocalDate now){
        int lengthOfMonth  = now.lengthOfMonth();
        int dayOfMonth = now.getDayOfMonth();
        log.info("lengthOfMonth:{},lengthOfMonth1:{}",lengthOfMonth,dayOfMonth);
        if(lengthOfMonth > 30 && dayOfMonth >30){
            dayOfMonth = 30;
        }
        return 30 - dayOfMonth +1 ;
    }

    public int getEndWithDays(LocalDate end){
        int lengthOfMonth  = end.lengthOfMonth();
        int dayOfMonth = end.getDayOfMonth();
        if(lengthOfMonth == dayOfMonth){
            dayOfMonth = 30;
        }else if(lengthOfMonth == 31 && dayOfMonth ==30){
            dayOfMonth = 29;
        }
        return dayOfMonth;
    }



}

@Data
class Form{
    private String begin;
    private String end;
}