package com.git.hui.offer.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author YiHui
 * @date 2025/7/27
 */
@Service
public class DateService {

    @Value("${qyplm.token}")
    private String authToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @Tool(description = "传入时区，返回对应时区的当前时间给用户")
    public String getTimeByZoneId(@ToolParam(description = "需要查询时间的时区") ZoneId area) {
        // 根据系统当前时间，获取指定时区的时间
        ZonedDateTime time = ZonedDateTime.now(area);

        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String ans = time.format(formatter);
        System.out.println("传入的时区是：" + area + "-" + ans);
        return ans;
    }

    @Tool(description = "获取50组电梯销售数据，用于拟合电梯价格的计算公式")
    public String getData1() {
        return "载重_kg\t速度_mps\t层站数\t提升高度_m\t井道宽_mm\t井道深_mm\t开门宽_mm\t开门高_mm\t轿厢深_mm\t轿厢宽_mm\t电梯类型\t驱动方式\t控制系统\t门机系统\t安全部件品牌\t主机品牌\t控制系统品牌\t智能化程度\t门保护装置\t能量回馈\t装潢等级\t地板材料\t吊顶类型\t操纵箱款式\t安装旧楼层\t需脚手架\t质保年\t维保类型\t颜色代码\t紧急交付\t价格_元\n" +
                "630\t1\t6\t18\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t120500\n" +
                "800\t1.75\t10\t30\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL7035\t否\t141200\n" +
                "1000\t2.5\t15\t45\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t永磁同步\t群控2台\t永磁同步门机\t进口\t西子\t新时达\t人脸派梯\t二维光幕\t有\t镜面蚀刻\t大理石\tLED艺术\t液晶触摸\t0\t否\t3\t全包\tRAL9001\t否\t228500\n" +
                "1350\t1\t8\t24\t2200\t2400\t1000\t2200\t1500\t1700\t货梯\t有齿轮\tPLC\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL9002\t否\t118300\n" +
                "1600\t0.5\t4\t12\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t液压\tPLC\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL9016\t否\t152600\n" +
                "450\t1\t5\t15\t1800\t1900\t700\t2000\t900\t1200\t乘客\t永磁同步\t单微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9003\t否\t104800\n" +
                "1000\t3\t20\t60\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t永磁同步\t群控3台\t永磁同步门机\t进口\t西子\t新时达\t目的层预约\t二维光幕\t有\t镜面蚀刻\t大理石\tLED艺术\t液晶触摸\t0\t否\t5\t全包\tRAL9010\t是\t298700\n" +
                "800\t1\t12\t36\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t物联网\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL7032\t否\t148600\n" +
                "630\t2.5\t18\t54\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t永磁同步\t双微机\t变频门机\t进口\t通润\t新时达\t无\t光幕\t有\t镜面蚀刻\t橡胶\t艺术造型\t不锈钢\t0\t否\t3\t半包\tRAL9010\t否\t211400\n" +
                "1350\t1.75\t14\t42\t2200\t2400\t1000\t2200\t1500\t1700\t医用\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t全包\tRAL9010\t否\t216500\n" +
                "1000\t1\t7\t21\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t有齿轮\t单微机\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL9002\t否\t112800\n" +
                "1600\t2.5\t22\t66\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t永磁同步\t双微机\t变频门机\t合资\t通润\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9016\t否\t242600\n" +
                "450\t1.75\t9\t27\t1800\t1900\t700\t2000\t900\t1200\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t133500\n" +
                "800\t3\t25\t75\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t群控2台\t永磁同步门机\t进口\t西子\t新时达\t人脸派梯\t二维光幕\t有\t镜面蚀刻\t大理石\tLED艺术\t液晶触摸\t0\t否\t3\t全包\tRAL9001\t否\t301200\n" +
                "630\t0.5\t3\t9\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t永磁同步\t单微机\t变频门机\t国产\t国产\t默纳克\t无\t光幕\t无\t标准喷涂\tPVC\t标准平板\t不锈钢\t0\t否\t1\t不含\tRAL7035\t否\t92400\n" +
                "1000\t4\t30\t90\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t永磁同步\t群控4台\t永磁同步门机\t进口\t西子\t新时达\t目的层预约\t二维光幕\t有\t蚀刻+木饰\t大理石\t艺术造型\t嵌入式\t0\t否\t5\t全包\tRAL9010\t否\t412500\n" +
                "1600\t1\t6\t18\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t有齿轮\tPLC\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL9002\t否\t136800\n" +
                "1350\t2.5\t20\t60\t2200\t2400\t1000\t2200\t1500\t1700\t医用\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t物联网\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t268400\n" +
                "800\t1.75\t8\t24\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL7035\t否\t139800\n" +
                "450\t2.5\t14\t42\t1800\t1900\t700\t2000\t900\t1200\t乘客\t永磁同步\t双微机\t变频门机\t进口\t蒙特拉利\t新时达\t无\t光幕\t有\t镜面蚀刻\t橡胶\t艺术造型\t不锈钢\t0\t否\t3\t半包\tRAL9003\t否\t185600\n" +
                "1000\t1\t10\t30\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t有齿轮\t单微机\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL9016\t否\t118700\n" +
                "1600\t3\t28\t84\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t永磁同步\t双微机\t变频门机\t进口\t通润\t新时达\t人脸派梯\t二维光幕\t有\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t3\t全包\tRAL9010\t否\t346200\n" +
                "630\t1.75\t11\t33\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t146300\n" +
                "800\t0.5\t4\t12\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t单微机\t变频门机\t国产\t国产\t默纳克\t无\t光幕\t无\t标准喷涂\tPVC\t标准平板\t不锈钢\t0\t否\t1\t不含\tRAL7032\t否\t99800\n" +
                "1350\t3\t24\t72\t2200\t2400\t1000\t2200\t1500\t1700\t医用\t永磁同步\t群控2台\t永磁同步门机\t进口\t西子\t新时达\t物联网\t二维光幕\t有\t镜面蚀刻\t大理石\tLED艺术\t液晶触摸\t0\t否\t4\t全包\tRAL9001\t否\t374500\n" +
                "1000\t1.75\t16\t48\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t183200\n" +
                "450\t3\t22\t66\t1800\t1900\t700\t2000\t900\t1200\t乘客\t永磁同步\t双微机\t变频门机\t进口\t蒙特拉利\t新时达\t目的层预约\t二维光幕\t有\t镜面蚀刻\t橡胶\t艺术造型\t不锈钢\t0\t否\t3\t全包\tRAL9002\t否\t267800\n" +
                "1600\t1.75\t12\t36\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t永磁同步\t双微机\t变频门机\t合资\t通润\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9016\t否\t204500\n" +
                "630\t2.5\t19\t57\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t永磁同步\t双微机\t变频门机\t进口\t蒙特拉利\t新时达\t无\t光幕\t有\t镜面蚀刻\t橡胶\t艺术造型\t不锈钢\t0\t否\t3\t半包\tRAL9010\t否\t218900\n" +
                "1000\t2.5\t18\t54\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t永磁同步\t群控2台\t永磁同步门机\t进口\t西子\t新时达\t人脸派梯\t二维光幕\t有\t蚀刻+木饰\t大理石\tLED艺术\t液晶触摸\t0\t否\t3\t全包\tRAL9001\t否\t287600\n" +
                "1000\t1.75\t12\t36\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t物联网\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t169800\n" +
                "800\t2.5\t18\t54\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t群控2台\t永磁同步门机\t进口\t西子\t新时达\t人脸派梯\t二维光幕\t有\t镜面蚀刻\t大理石\tLED艺术\t液晶触摸\t0\t否\t3\t全包\tRAL9001\t否\t268500\n" +
                "630\t1\t7\t21\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t有齿轮\t单微机\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL7035\t否\t105200\n" +
                "1600\t0.5\t5\t15\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t液压\tPLC\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL9016\t否\t159800\n" +
                "450\t2.5\t16\t48\t1800\t1900\t700\t2000\t900\t1200\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9003\t否\t162400\n" +
                "1350\t3\t26\t78\t2200\t2400\t1000\t2200\t1500\t1700\t医用\t永磁同步\t群控3台\t永磁同步门机\t进口\t西子\t新时达\t目的层预约\t二维光幕\t有\t镜面蚀刻\t大理石\tLED艺术\t液晶触摸\t0\t否\t4\t全包\tRAL9010\t否\t398200\n" +
                "800\t1\t9\t27\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t单微机\t变频门机\t国产\t国产\t默纳克\t无\t光幕\t无\t标准喷涂\tPVC\t标准平板\t不锈钢\t0\t否\t1\t不含\tRAL9002\t否\t112600\n" +
                "1000\t4\t32\t96\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t永磁同步\t群控4台\t永磁同步门机\t进口\t西子\t新时达\t人脸派梯\t二维光幕\t有\t蚀刻+木饰\t大理石\t艺术造型\t嵌入式\t0\t否\t5\t全包\tRAL9001\t否\t435800\n" +
                "630\t1.75\t13\t39\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t153600\n" +
                "1600\t2.5\t24\t72\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t永磁同步\t双微机\t变频门机\t合资\t通润\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9016\t否\t258900\n" +
                "450\t1\t4\t12\t1800\t1900\t700\t2000\t900\t1200\t乘客\t永磁同步\t单微机\t变频门机\t国产\t国产\t默纳克\t无\t光幕\t无\t标准喷涂\tPVC\t标准平板\t不锈钢\t0\t否\t1\t不含\tRAL7032\t否\t87400\n" +
                "1000\t2.5\t14\t42\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t永磁同步\t双微机\t变频门机\t进口\t蒙特拉利\t新时达\t无\t光幕\t有\t镜面蚀刻\t橡胶\t艺术造型\t不锈钢\t0\t否\t3\t半包\tRAL9010\t否\t226800\n" +
                "800\t3\t28\t84\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t群控2台\t永磁同步门机\t进口\t西子\t新时达\t物联网\t二维光幕\t有\t镜面蚀刻\t大理石\tLED艺术\t液晶触摸\t0\t否\t3\t全包\tRAL9001\t否\t322500\n" +
                "1350\t1\t10\t30\t2200\t2400\t1000\t2200\t1500\t1700\t医用\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t186400\n" +
                "630\t0.5\t3\t9\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t有齿轮\t单微机\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL9016\t否\t85600\n" +
                "1600\t1.75\t15\t45\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t永磁同步\t双微机\t变频门机\t进口\t通润\t新时达\t无\t光幕\t有\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t229700\n" +
                "1000\t1\t8\t24\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t有齿轮\t单微机\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL7035\t否\t115300\n" +
                "800\t2.5\t20\t60\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9002\t否\t192500\n" +
                "450\t3\t25\t75\t1800\t1900\t700\t2000\t900\t1200\t乘客\t永磁同步\t群控2台\t永磁同步门机\t进口\t蒙特拉利\t新时达\t人脸派梯\t二维光幕\t有\t镜面蚀刻\t橡胶\t艺术造型\t不锈钢\t0\t否\t3\t全包\tRAL9003\t否\t276400\n" +
                "1350\t1.75\t12\t36\t2200\t2400\t1000\t2200\t1500\t1700\t医用\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t物联网\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t227800";
    }

    @Tool(description = "获取10组电梯销售数据，用于校验电梯价格的计算公式")
    public String getData2() {
        return "载重_kg\t速度_mps\t层站数\t提升高度_m\t井道宽_mm\t井道深_mm\t开门宽_mm\t开门高_mm\t轿厢深_mm\t轿厢宽_mm\t电梯类型\t驱动方式\t控制系统\t门机系统\t安全部件品牌\t主机品牌\t控制系统品牌\t智能化程度\t门保护装置\t能量回馈\t装潢等级\t地板材料\t吊顶类型\t操纵箱款式\t安装旧楼层\t需脚手架\t质保年\t维保类型\t颜色代码\t紧急交付\t价格_元\n" +
                "630\t4\t35\t105\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t永磁同步\t群控3台\t永磁同步门机\t进口\t西子\t新时达\t目的层预约\t二维光幕\t有\t镜面蚀刻\t大理石\tLED艺术\t液晶触摸\t0\t否\t4\t全包\tRAL9001\t否\t398500\n" +
                "1000\t1.75\t11\t33\t2100\t2300\t900\t2200\t1400\t1600\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9016\t否\t162400\n" +
                "1600\t3\t30\t90\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t永磁同步\t群控2台\t永磁同步门机\t进口\t西子\t新时达\t人脸派梯\t二维光幕\t有\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t3\t全包\tRAL9010\t否\t378600\n" +
                "800\t1\t6\t18\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t单微机\t变频门机\t国产\t国产\t默纳克\t无\t光幕\t无\t标准喷涂\tPVC\t标准平板\t不锈钢\t0\t否\t1\t不含\tRAL7032\t否\t105800\n" +
                "450\t2\t12\t36\t1800\t1900\t700\t2000\t900\t1200\t乘客\t永磁同步\t双微机\t变频门机\t合资\t蒙特拉利\t默纳克\t无\t光幕\t无\t发纹不锈钢\tPVC\t标准平板\t不锈钢\t0\t否\t2\t半包\tRAL9010\t否\t138500\n" +
                "1350\t2.5\t22\t66\t2200\t2400\t1000\t2200\t1500\t1700\t医用\t永磁同步\t双微机\t变频门机\t进口\t西子\t新时达\t物联网\t二维光幕\t有\t镜面蚀刻\t大理石\tLED艺术\t液晶触摸\t0\t否\t3\t全包\tRAL9001\t否\t336700\n" +
                "1000\t0.5\t4\t12\t2100\t2300\t900\t2200\t1400\t1600\t货梯\t有齿轮\tPLC\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL9016\t否\t112400\n" +
                "630\t3\t30\t90\t1900\t2100\t800\t2100\t1100\t1400\t乘客\t永磁同步\t群控2台\t永磁同步门机\t进口\t蒙特拉利\t新时达\t人脸派梯\t二维光幕\t有\t镜面蚀刻\t橡胶\t艺术造型\t不锈钢\t0\t否\t3\t全包\tRAL9002\t否\t326800\n" +
                "1600\t1\t8\t24\t2300\t2500\t1100\t2200\t1600\t1800\t货梯\t液压\tPLC\t机械门机\t国产\t国产\t蓝光\t无\t安全触板\t无\t标准喷涂\tPVC\t标准平板\t塑料\t0\t否\t1\t不含\tRAL7035\t否\t148500\n" +
                "800\t4\t40\t120\t2000\t2200\t900\t2100\t1300\t1500\t乘客\t永磁同步\t群控4台\t永磁同步门机\t进口\t西子\t新时达\t目的层预约\t二维光幕\t有\t蚀刻+木饰\t大理石\tLED艺术\t嵌入式\t0\t否\t5\t全包\tRAL9010\t否\t468200";
    }

    @Tool(description = "从PLM系统查询任务列表，支持分页查询")
    public String retrieveTaskList(
            @ToolParam(description = "页码，从1开始") int pageNum,
            @ToolParam(description = "每页条数") int pageCount) {

        System.out.println("[🔨] 查询任务列表，pageNum=" + pageNum + ", pageCount=" + pageCount);

        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://139.159.221.11:9002/qyplmapi/udscfg-plm/task/retrieveTaskList")
                    .queryParam("pageCount", pageCount)
                    .queryParam("pageNum", pageNum)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>("{}", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String result = response.getBody();
            System.out.println("[✅] 任务列表查询成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 查询任务列表失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统查询产品列表，支持分页和搜索")
    public String retrieveProductList(
            @ToolParam(description = "搜索文本，可为空") String searchText,
            @ToolParam(description = "分组代码，可为空") String groupCodes,
            @ToolParam(description = "页码，从1开始") int pageIndex,
            @ToolParam(description = "每页条数") int pageSize) {

        System.out.println("[🔨] 查询产品列表，searchText=" + searchText + ", groupCodes=" + groupCodes + ", pageIndex=" + pageIndex + ", pageSize=" + pageSize);

        try {
            String url = UriComponentsBuilder.fromHttpUrl("http://139.159.221.11:9002/qyplmapi/-param/api/Product")
                    .queryParam("searchText", searchText != null ? searchText : "")
                    .queryParam("groupCodes", groupCodes != null ? groupCodes : "")
                    .queryParam("pageIndex", pageIndex)
                    .queryParam("pageSize", pageSize)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>("{}", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String result = response.getBody();
            System.out.println("[✅] 产品列表查询成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 查询产品列表失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "获取产品下的参数列表，需要先查询产品版本，再根据版本ID获取参数")
    public String retrieveProductParams(
            @ToolParam(description = "产品编码，如 PD00002") String productCode,
            @ToolParam(description = "搜索文本，可为空") String searchText,
            @ToolParam(description = "分组代码，可为空") String groupCodes,
            @ToolParam(description = "页码，从1开始") int pageIndex,
            @ToolParam(description = "每页条数") int pageSize) {

        System.out.println("[🔨] 查询产品参数，productCode=" + productCode);

        try {
            // 第一步：获取产品版本信息
            String configUrl = UriComponentsBuilder.fromHttpUrl("http://139.159.221.11:9002/qyplmapi/-param/api/ConfigBank")
                    .queryParam("productCode", productCode)
                    .queryParam("pageIndex", 1)
                    .queryParam("pageSize", 50)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<String> configResponse = restTemplate.exchange(
                    configUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String configResult = configResponse.getBody();
            System.out.println("[✅] 产品版本信息查询成功");

            // 解析版本ID（简化处理，实际应该使用 JSON 解析）
            String versionId = extractVersionId(configResult);
            if (versionId == null) {
                return "未找到产品版本信息";
            }
            System.out.println("[ℹ️] 获取到版本ID: " + versionId);

            // 第二步：根据版本ID获取参数列表
            String paramUrl = UriComponentsBuilder.fromHttpUrl(
                            "http://139.159.221.11:9002/qyplmapi/-param/api/ConfigBank/" + versionId + "/Param")
                    .queryParam("searchText", searchText != null ? searchText : "")
                    .queryParam("groupCodes", groupCodes != null ? groupCodes : "")
                    .queryParam("pageIndex", pageIndex)
                    .queryParam("pageSize", pageSize)
                    .toUriString();

            ResponseEntity<String> paramResponse = restTemplate.exchange(
                    paramUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String result = paramResponse.getBody();
            System.out.println("[✅] 产品参数列表查询成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 查询产品参数失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统根据对象名称查询对象实例列表")
    public String retrieveObjectByName(
            @ToolParam(description = "对象名称，如 260524-1") String objectName) {

        System.out.println("[🔨] 查询对象信息，objectName=" + objectName);

        try {
            String url = "http://139.159.221.11:9002/qyplmapi/accessbusiness/item/instance/action/listbatch/page";

            String requestBody = String.format(
                    "{\"pageNum\":1,\"pageSize\":10,\"searchRevisionTypeEnum\":\"ISLATESTONLY\",\"isSearchChildren\":false,\"orderAttributes\":[],\"objectEntries\":[],\"orSegmentGroup\":{\"andSegmentGroups\":[{\"generalSegments\":[{\"operationKey\":\"EQ\",\"attribute\":\"objectName\",\"value\":\"%s\"}],\"operationKey\":\"AND\"}],\"operationKey\":\"OR\"}}",
                    objectName != null ? objectName.replace("\\", "\\\\").replace("\"", "\\\"") : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String result = response.getBody();
            System.out.println("[✅] 对象信息查询成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 查询对象信息失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统删除对象实例")
    public String deleteObject(
            @ToolParam(description = "对象GUID") String guid,
            @ToolParam(description = "业务对象GUID，对应bizObjectGuid") String businessObjectGuid) {

        System.out.println("[🔨] 删除对象，guid=" + guid + ", businessObjectGuid=" + businessObjectGuid);

        try {
            String url = "http://139.159.221.11:9002/qyplmapi/accessbusiness/item/instance";

            String requestBody = String.format(
                    "{\"bizObjectGuid\":\"%s\",\"guid\":\"%s\"}",
                    businessObjectGuid != null ? businessObjectGuid.replace("\\", "\\\\").replace("\"", "\\\"") : "",
                    guid != null ? guid.replace("\\", "\\\\").replace("\"", "\\\"") : ""
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    String.class
            );

            String result = response.getBody();
            System.out.println("[✅] 对象删除成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 删除对象失败: " + e.getMessage());
            return "删除失败: " + e.getMessage();
        }
    }

    /**
     * 从版本信息响应中提取第一个版本数据的ID
     */
    private String extractVersionId(String configResult) {
        if (configResult == null || configResult.isEmpty()) {
            return null;
        }
        try {
            // 简单字符串提取，查找第一个 "id": "xxx" 的模式
            int idIndex = configResult.indexOf("\"id\":");
            if (idIndex == -1) {
                return null;
            }
            int start = configResult.indexOf("\"", idIndex + 5);
            int end = configResult.indexOf("\"", start + 1);
            if (start != -1 && end != -1) {
                return configResult.substring(start + 1, end);
            }
        } catch (Exception e) {
            System.err.println("解析版本ID失败: " + e.getMessage());
        }
        return null;
    }
}
