package com.git.hui.offer.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YiHui
 * @date 2025/7/27
 */
@Service
public class DateService {

    private String authToken;

    @Value("${qyplm.base-url:http://139.159.221.11:9002}")
    private String baseUrl;

    private final Environment environment;
    private final ObjectMapper objectMapper;
    private Map<String, String> plmPages = new HashMap<>();

    public DateService(Environment environment, ObjectMapper objectMapper) {
        this.environment = environment;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void initPlmPages() {
        List<String> pageJsonList = Binder.get(environment)
                .bind("qyplm.pages", Bindable.listOf(String.class))
                .orElse(new ArrayList<>());
        for (String json : pageJsonList) {
            try {
                Map<String, String> map = objectMapper.readValue(json, Map.class);
                plmPages.putAll(map);
            } catch (Exception e) {
                System.err.println("解析页面配置失败: " + json + ", " + e.getMessage());
            }
        }

        refreshAuthToken();

    }

    private final RestTemplate restTemplate = new RestTemplate();

    @Tool(description = "更新参数值")
    public String updateParamData(@ToolParam(description = "参数Map") Map<String, String> paramMap) {
        System.out.println("传入的数据为：" + JsonParser.toJson(paramMap));
        return "更新成功";
    }

    @Tool(description = "更新提示信息")
    public String updateParamTips(@ToolParam(description = "参数提示信息") List<String> tips) {
        System.out.println("传入的数据为：" + JsonParser.toJson(tips));
        return "更新成功";
    }

    @Tool(description = "传入数据ID，获取参数详情")
    public String getParamMsg(@ToolParam(description = "数据ID") String dataID) {
        System.out.println("传入的数据ID是：" + dataID);

        String message = "## 参数名称：制造厂家\n" +
                "\n" +
                "## 参数解读提示信息\n" +
                "1.去除上海电气集团上海电机厂有限公司\n" +
                "2.湘潭的公司要排在前面\n" +
                "\n" +
                "## 已经解读出来的参数值和关联信息\n" +
                "参数值：湘潭电机股份有限公司\n" +
                "章节：2.7.3.3 电机电数据表\n" +
                "原因：文档中电机数据表显示YXKK400-4的制造厂家为上海电气集团上海电机厂有限公司、佳木斯电机股份有限公司、湘潭电机股份有限公司，根据提示信息去除上海电气集团上海电机厂有限公司，且湘潭的公司要排在前面，因此制造厂家为湘潭电机股份有限公司。";

        return message;
    }

    @Tool(description = "传入数据ID，获取文档内容")
    public String getDoc(@ToolParam(description = "数据ID") String dataID) {
        System.out.println("传入的数据ID是：" + dataID);

        String message = "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂2×660MW煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "## 附件 1 高压电动机技术协议\n" +
                "## 1. 总则\n" +
                "1.1. 本设备技术协议书适用于内蒙古汇能长川发电有限公司汇能集团长滩电厂 $2 \\times$ 660MW 煤电一体化扩建项目使用的 $10 \\mathrm{kV} 200 \\mathrm{kW}$ 及以上高压电动机及其附件的订货，它包括电机本体及辅助设备的功能设计、结构、性能、安装和试验等方面的技术要求和相关要求。\n" +
                "1.2. 本协议书提出的是最低限度的技术要求，并未对一切技术细节作出规定，也未充分引述有关标准和协议的条文，卖方应保证提供符合本协议书和工业标准的优质产品。\n" +
                "1.3. 如果卖方没有以书面形式对本协议书的条文提出异议，则意味着卖方提出的设备完全符合本协议书的要求。如有异议，不管多么微小，都应在报价书中以“对协议书的意见和同协议书的差异”为标题的专门章节中加以详细描述。\n" +
                "1.4. 本设备技术协议书中所使用的标准之间发生矛盾、或与卖方所执行的标准不一致时，按较高标准执行。\n" +
                "1.5. 在签订合同之后，买方有权提出因协议标准和规程发生变化而产生的一些补充要求，具体项目由买卖双方共同商定。\n" +
                "1.6. 本设备技术协议书经买卖双方确认后做为订货合同的技术附件，与合同正文具有同等效力。\n" +
                "1.7. 本工程统一规定：额定功率为 $200 \\mathrm{kW}$ 及以上的电动机采用 $10 \\mathrm{kV}$ 电压等级，额定功率为 $200 \\mathrm{kW}$ 以下的电动机采用 $380 \\mathrm{~V}$ 电压等级。\n" +
                "1.8. 卖方负责被驱动设备与电动机的总体归口。电动机的设计、构造及与被驱动设备的连接等，必须与它所驱动设备的相关需要、运行条件和维护要求等一致。\n" +
                "1.9. 卖方提供电动机满足 GB 30254-2013《高压三相笼型异步电动机能效限定值及能效等级》（非变频拖动的电动机），必须达到国标二级能效，严禁使用国家已淘汰电动机和高耗能电动机。\n" +
                "1.10. 变频拖动的电动机采用变频电机。\n" +
                "1.11. 本设备技术协议书未尽事宜，由买卖双方共同协商确定。\n" +
                "1. 12. 本工程采用设备标识系统。卖方在协议签订后提供的技术资料（包括图纸）和设备的标识必须有电厂标识系统编码，卖方在设计，制造、运输、安装、运行及项目管理的各个环节均使用编码，系统的编制原则及具体标识由设计院编制提出。\n" +
                "2. 技术要求\n" +
                "刘\n" +
                "谭\n" +
                "$\\frac{1}{2}x$ \n" +
                "61 \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂 $2 \\times 660\\mathrm{MW}$ 煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "## 2.1. 协议和标准\n" +
                "卖方所提供的电动机应满足下面所列协议和标准的要求，但不限于此。\n" +
                "GB755 旋转电机定额和性能\n" +
                "GB/T997 电机结构及安装型式代号\n" +
                "GB1971 电机线端标志与旋转方向\n" +
                "GB1993 电机冷却方法\n" +
                "GB4942.1 电机外壳分级\n" +
                "GB10069.1 旋转电机噪声测定方法及限值\n" +
                "GB/T 1032 三相异步电机试验方法\n" +
                "GB4942.1 旋转电机外壳防护等级(IP代码)分级\n" +
                "GB/T 13957 大型三相异步电动机基本系列技术条件\n" +
                "GB 30254 高压三相笼型异步电动机能效限定值及能效等级\n" +
                "GB 10068 轴中心高为 $56 \\mathrm{~mm}$ 及以上电机的机械振动\n" +
                "## 2.2. 工程条件\n" +
                "## 2.2.1. 系统概况：\n" +
                "系统额 定电压: 10 kV\n" +
                "系统最高电压： 12 kV\n" +
                "系统额定频率： 50Hz\n" +
                "系统中性点接地方式：中阻接地，接地电阻 60 欧姆\n" +
                "本工程海拔： 1200m\n" +
                "2.2.2. 安装地点：户内\n" +
                "2.3. 电动机基本参数\n" +
                "2.3.1. 电动机型式: YXKK355-4 / YXKK400-4\n" +
                "2.3.2. 额定功率: 280 / 630 kW\n" +
                "2.3.3. 额定电压： 10 kV\n" +
                "2.3.4. 最高运行电压：12 kV\n" +
                "2.3.5. 额定耐受试验电压: 21 kV\n" +
                "2.3.6. 额定频率： $50\\mathrm{Hz}$ \n" +
                "2.3.7. 额定转速: ____ r/min\n" +
                "2.3.8. 转动惯量: $\\mathrm{kgm}^{2}$ \n" +
                "刘鈞\n" +
                "62 \n" +
                "式\n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂2×660MW煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "## 2.4. 技术性能要求\n" +
                "2.4.1. 协议书中设备的参数均按海拔 1000 米以下提出, 卖方应对所提出参数按照 1200 米海拔值进行修正, 修正系数满足国标 GB311.1《高压输变电设备的绝缘配合》及相关标准的要求。卖方需按照国家标准提供必要的防止电机电晕的措施。卖方应考虑高海拔地区电机降容问题, 需与泵（风机）功率相匹配。\n" +
                "2.4.2. 电动机的设计应符合本技术协议书和被驱动设备制造厂商提出的特定使用要求。当运行在设计条件下时，电动机铭牌出力应不小于被驱动设备所需功率的 $1.1 \\sim 1.15$ （泵类）、 $1.05 \\sim 1.1$ （风机）。\n" +
                "2.4.3. 电动机为交流异步电动机。电动机应能在电源电压变化为额定电压的±10%内，或频率变化为额定频率的±5%内，或电压和频率同时改变，但变化之和的绝对值在10%内时连续满载运行。\n" +
                "2.4.4. 电动机采用直接起动式，能按被驱动设备的转速—转矩曲线所示的载荷进行成功的起动。当电源电压降低到额定电压的 $70\\%$ 时，电动机应能实现自动起动。\n" +
                "2.4.5. 电动机的起动电流应达到满足性能要求与经济设计一致的最低电流值。在额定电压条件下，电动机的最大起动电流不得超过其额定电流的6.5倍保证值。\n" +
                "2.4.6. 在规定的起动电压的极限值范围之内，电动机转子允许起动时间不得低于其加速时间。\n" +
                "2.4.7. 电动机在冷态下起动应不多于 2 次，每次的起动循环周期不小于 5 分钟；热态起动应不多于 1 次。如果起动时间不超过 2～3 秒，电动机应能够多次起动。\n" +
                "2.4.8. 在额定功率下运行时，电动机应能承受电源快速切换过程中的电源中断而不损坏（假定原有电源与新通电源在切换之前是同步的）。\n" +
                "2.4.9. 电动机绝缘等级为 F 级，但其温升不得超过 B 级绝缘规定的温升值。电动机绕组采用真空压力浸渍处理和环氧树脂密封绝缘。绝缘应能承受周围环境的影响。电动机的引线与绕组的绝缘应具有相同的绝缘等级。\n" +
                "2.4.10. 电动机定子槽楔采用磁性槽楔。电动机转子鼠笼条采用铜材，并有防止笼条移位、跳出、断裂的措施。\n" +
                "2.4.11. 电动机应能承受规定的过电压要求。如果另需采取保护措施，卖方应以书面方式提出，并由买方认可。\n" +
                "2.4.12. 电动机的结构应能耐受标准规定的正反转的超速值，而不造成设备损坏。\n" +
                "2.4.13. 电动机的振动幅度不应超过标准所规定的数值。\n" +
                "刘. 鲍.\n" +
                "译\n" +
                "赵\n" +
                "63 \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂2×660MW煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "2.4.14. 电动机的最高噪音水平应符合所列协议和标准的要求。距外壳1米远处，电动机的平均声级不得大于85dB（A声级）。\n" +
                "2.4.15. 电动机内部引线与外部电缆连接的接线端子、过渡铜排、螺栓、垫片等均由卖方负责提供。电动机引线引向接线盒时必须装有防止磨损的绝缘固定夹件。电动机引线的支持绝缘子采用瓷质材料，电机引线及外接电缆螺栓随电动机厂家提供。\n" +
                "2.4.16. 在现场和规定的环境中完全符合协议运行条件下，电动机的设计应能保证其使用寿命不低于30年。\n" +
                "2.4.17. 对于装有防滴式外壳的电动机，应采用弹性耐磨涂层对定子绕组的端部和通风槽进行处理。\n" +
                "2.4.18. 电动机的空载电流小于其额定电流的 $35\\%$ 。\n" +
                "2.4.19. 电动机出厂时应提供模拟在线运行的试验报告。\n" +
                "2.4.20. 控制方式若采用变频控制，应充分考虑变频器功率和电动机的匹配，电动机选型为满足变频调速技术要求的变频电机。电机在结构设计时，应充分考虑非正弦电源特性影响绝缘结构、振动、噪声等，有抑制电流中的高次谐波的措施，电动机漏抗的大小要兼顾到整个调速范围内阻抗匹配的合理性。电动机机械部分的回转部件必须符合变频调速运行工况的要求。若采用独立的冷却方式，卖方应在协议中提出冷却风机的容量及电源要求，冷却电机选型须买方确定。\n" +
                "1.1.1. 10kV 电动机要求选用上海电气集团上海电机厂有限公司、佳木斯电机股份有限公司、湘潭电机股份有限公司（注：湘潭电机股份有限公司产品湘潭电机按投运后 36 个月质保。）产品，最终由买方确定。\n" +
                "1.1.2. 10kV 电动机滚动轴承采用进口轴承，选型范围：SKF（原产地欧美国家）产品。配套厂家必须提供进口轴承原产地证明和进口报关单。\n" +
                "2.4.21. 电动机的颜色应与周围环境协调，具体由买方确定。\n" +
                "2.5. 设计与结构要求\n" +
                "2.5.1. 外壳的通风与保护\n" +
                "2.5.1.1. 电动机采用鼠笼式结构，其外壳防护等级应不低于 IP____级（参见附录 A）。电动机的设计应达到风机设备所需要的任何特殊转矩要求。\n" +
                "2.5.1.2. 当通风要求设立隔栅时，隔栅应符合适用的标准，并应能够耐腐蚀。对于通风隔栅，应进行和电动机机座及外壳的油漆部分同样的防腐处理。为了检查和清扫电动机绕组和气隙，隔栅应能方便的拆卸。\n" +
                "1 \n" +
                "刘勋\n" +
                "64 \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂2×660MW煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "2.5.1.3. 电动机冷却方式采用空空冷方式。\n" +
                "## 2.5.2. 接地：\n" +
                "每台电动机机座应有两处接地，两个接地应位于电动机完全相反的两侧。对于立式电动机，一个接地装置位于电源电缆穿线盒的下方，另一个接地装置位于与第一个接地装置相差180度的位置。电动机接线盒内设单独接地端子。\n" +
                "## 2.5.3. 轴承和轴承盖\n" +
                "电动机轴承的结构应能排除尘垢和水份的侵入，并有防止润滑剂污染电动机绕组的措施。所有电动机轴承应与下列要求保持一致：\n" +
                "2.5.3.1. 在可以满足规定的用途、操作条件和外壳等项要求时，卧式电动机可装有套筒式轴承。立式电动机应装备带护罩的推力式轴承。\n" +
                "2.5.3.2. 除了压力润滑油以外，套筒式轴承应为油环式套筒轴承。装有套筒式轴承的电动机应具有容易拆卸的轴承、轴承箱、端罩或底座，以便检查和更换轴承时不必拆卸电动机或拆下电动机的联轴器。制造轴承的巴氏合金应符合相关标准。油环应为单片固定结构。应提供用于检查油环的装置。\n" +
                "2.5.3.3. 套筒式轴承应有接近气隙的简便方法，以便在不拆下轴承盖的情况下利用气隙测量仪检测轴承的磨损。\n" +
                "2.5.3.4. 提供的所有油位观察仪均应带有标志，以显示电动机在停用状态和运行状态的正确油位。如果两种状态下的油位之差是明显的。卖方应提供检查正常轴承滑油流动的方法。\n" +
                "2.5.3.5. 当采用压力油润滑的卧式电动机时，电动机轴承应为套筒式，压力油来自被驱动设备的润滑系统。当压力油系统不工作时，油环装置应足以满足电动机起动和至少1个半小时的连续运行要求。电机制造厂应提供润滑油流动指示计来指示每个电动机轴承流出的油流方向。\n" +
                "2.5.3.6. 提供电动机配套的轴承型号及电动机使用的润滑油型号。\n" +
                "2.5.3.7. 具有轴架式轴承的电动机应配有两个与基座绝缘的轴承轴架，并应在驱动端（联轴器端）的轴架上提供一个可拆卸的接地搭接片。\n" +
                "2.5.3.8. 电动机应配备润滑油加油嘴。\n" +
                "2.5.3.9. 耐磨轴承的电动机应在固定于电动机壳的铭牌上明确标示。耐磨轴承应达到150000小时的最低额定使用寿命。卖方应提供阐述确定轴承额定使用寿命所依据的资料以及这类电动机实际使用条件下的性能记录。\n" +
                "谢\n" +
                "刘钢\n" +
                "65 \n" +
                "古\n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂2×660MW煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "2.5.3.10. 卖方应在设备使用说明书中提供一份完整的推荐使用并完全适用的润滑油清单，包括其商标牌号和油品名称，并在电动机设备铭牌（可以使用单独的设备铭牌）上标明。\n" +
                "2.5.3.11. 电动机轴承润滑脂应满足正常投运后制造厂规定的一个加油周期的要求，在此期间，由于润滑脂的问题造成轴承甚至电动机损坏的情况，由卖方承担责任。\n" +
                "## 2.5.4. 联轴器\n" +
                "2.5.4.1. 套筒式轴承的电动机的设计应采用带有限制轴端浮动的联轴器，以防止被驱动设备将轴向推力传递给电动机轴承。电动机和联轴器的端部浮动应符合所列标准中的有关的规定。\n" +
                "2.5.4.2. 实心轴的立式电动机应具有一个符合被驱动设备制造厂商提供的尺寸要求的延伸轴。\n" +
                "2.5.5. 转向：\n" +
                "电动机旋转方向应有永久性、明显的标志，电动机应允许空载反转。电动机接线盒内应标明相序（A、B、C）。电动机的旋转方向应与所驱动设备保持匹配，卖方应与所驱动设备厂家配合，根据设备具体布置情况确定电动机的旋转方向。\n" +
                "## 2.5.6. 安装与装定位销\n" +
                "2.5.6.1. 除特殊应用外，卧式电动机应采用底脚安装方式，立式电动机应采用底座安装方式。卖方应与被驱动设备制造厂商协调安装的细节。\n" +
                "2.5.6.2. 电动机的设计应便于通过电动机底座或安装法兰钻孔（最好是垂直钻孔），以便电动机与被驱动设备安装好后装入定位销钉。\n" +
                "2.5.6.3. 当因电动机结构的限制而使垂直销钉无法安装时，电动机底座与轴垂直方向应加工或浇注为一个按销钉允许最小的角度，并提供一个导向角。\n" +
                "## 2.5.7. 排水孔\n" +
                "每台电动机应设有一个排水孔，以防内部水的积聚。\n" +
                "2.6. 仪表和控制要求\n" +
                "2.6.1. 温度检测器\n" +
                "2.6.1.1. 每台电动机应装设电阻式温度检测器（RTD）。6个（每相绕组2个）；\n" +
                "2.6.1.2. 电阻式温度检测器应埋入定子绕组中的局部温度最高的部位。温度检测器的感温元件为3线式的Pt-100（在 $0^{\\circ}\\mathrm{C}$ 时的额定阻值为100欧的铂金）。\n" +
                "2.6.1.3. 每个RTD的引线端子应带有识别标志，以便通过对照电动机简图便能确定每\n" +
                "307 \n" +
                "f(x) \n" +
                "66 \n" +
                "内蒙古汇能长川发电有限公司汇能集团\n" +
                "长滩电厂 2×660MW 煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "个RTD的位置。\n" +
                "## 2.6.1.4. 轴承热电偶或电阻式温度检测器\n" +
                "1) 电动机轴承应装设金属热电偶或电阻式温度检测器。(仅对于装设水平套筒式轴承或推力轴承, 或立式旋转设备中的推力轴承或平面轴承的电动机), 电阻式温度检测器的感温元件为 $3$ 线式的 $\\mathrm{Pt}-100$ (在 $0^{\\circ} \\mathrm{C}$ 时的额定阻值为 100 欧的铂金)。\n" +
                "2) 轴承的金属热电偶应符合如下设计与结构特点: 封闭轴承的热电偶组件应可以从机器外面进行检查和更换; 与设备机座绝缘的轴承, 必须使其热电偶的热接点与屏蔽套断开 (即氧化镁包装的顶部)。卖方的责任是不得损坏电气绝缘隔层, 保持轴承与地线之间的绝缘。其它所有热电偶必须接地。\n" +
                "3）热电偶应采用不锈钢制成的套管，由弹簧加载，除非另有规定，热电偶应采用镍铬合金---康铜丝，封包在氧化镁内部。\n" +
                "2.6.2. 对于 2000kW 及以上的电动机，电动机出线侧和中性点侧卖方各提供 3 个用于差动保护的电流互感器以及相应的接线盒和封闭外罩，中性点 CT 连接铜排（电缆）绝缘应符合国家标准，连接铜排（电缆）由卖方提供，差动保护用电流互感器的二次电流为 1A，额定容量为 15VA，准确级为 5P30（暂定，与高压开关柜保持一致）（CT 选型：大连第一互感器厂）。\n" +
                "2.6.3. 如果电动机设有油站，卖方提供油站就地电控箱（柜），用于电动机油站保护、联锁、控制及报警，并留有与 DCS 的信号接口。油站成套的温度、压力、液位仪表均接至相应的接线盒内。\n" +
                "## 2.7. 电机附件\n" +
                "2.7.1. 加热器\n" +
                "2.7.1.1. 卖方应设计并提供电动机内部加热器，以防止电动机停运时电动机内部潮湿和凝露。加热器应安装在电动机内部可检查的部位。\n" +
                "2.7.1.2. 加热器的电源：\n" +
                "功率在 2300W 以下时，单相、220V 交流；功率大于 2300W 时，三相、380V 交流。\n" +
                "## 2.7.2. 接线盒和接线板\n" +
                "2.7.2.1. 安装在电动机机座上的单独的可检查的接线盒应有下列四种引线：\n" +
                "a) 电动机的主引线;\n" +
                "b) 电动机内部加热器的引线；\n" +
                "c) 电阻式温度检测器 RTD 和（或）热电偶的引线；\n" +
                "刘凯.\n" +
                "谢\n" +
                "赵\n" +
                "67 \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂2×660MW煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "d) 电流互感器 CT（二次回路）的引线（仅用于 2000kW 及以上电动机）。\n" +
                "2.7.2.2. 电动机电源回路主引线的接线盒应采用下进线方式，设计上应方便拆装和电缆接入。有差动保护的中性点 CT 接线盒采用不落地式，与主引线接线盒异侧布置。\n" +
                "2.7.2.3. 对于卧式电动机，除非特殊情况，主引线的接线盒从电动机头部看应安装在电动机的右侧。当多路电缆导线管端接于电动机接线盒，且所有三相导线并不是穿入每根导线管时，导线管的一侧侧板必须使用非磁性材料。\n" +
                "2.7.2.4. 相对于主引线接线盒，立式电动机的热保护装置的接线盒应是顺时针方向约 $45^{\\circ}\\sim90^{\\circ}$ （俯视）；加热器的接线盒应是逆时针方向约 $45^{\\circ}\\sim90^{\\circ}$ 。所有其它装置的配置应由买方审定。\n" +
                "2.7.2.5. 电动机主引线接线盒的尺寸必须满足电气绝缘的要求，同时应考虑方便拆接线，其最小尺寸见下表，单位为 mm。\n" +
                "<table><tr><td colspan=\"2\">单位(mm)</td><td colspan=\"6\">电动机额定电压</td></tr><tr><td rowspan=\"2\">电缆尺寸</td><td rowspan=\"2\">每相导体</td><td colspan=\"3\">6000V以下</td><td colspan=\"3\">10000V及以下</td></tr><tr><td>长(L)</td><td>宽(W)</td><td>高(D)</td><td>长(L)</td><td>宽(W)</td><td>高(D)</td></tr><tr><td>90mm~185mm</td><td>1</td><td>650</td><td>300</td><td>360</td><td>720</td><td>300</td><td>360</td></tr><tr><td>240mm~400mm</td><td>1</td><td>650</td><td>360</td><td>410</td><td>720</td><td>360</td><td>410</td></tr><tr><td>240mm~400mm</td><td>2</td><td>650</td><td>450</td><td>410</td><td>850</td><td>450</td><td>410</td></tr></table>\n" +
                "当电缆接线盒内需要安装附加装置，上述尺寸应增大。\n" +
                "2.7.2.6. 当电动机每相需要两根电缆时，其主引线接线盒的宽度最小应增大到 740mm。端子排的排列应为每组的三相端子从左向右排一行，依次为 T1、T2、T3、T3A、T2A 和 T1A。\n" +
                "## 2.7.3. 油站控制箱\n" +
                "2.7.3.1 如果电动机设有油站，卖方提供油站就地电控箱（柜），用于电动机油站保护、联锁、控制及报警，并留有与 DCS 的信号接口。油站成套的温度、压力、液位仪表均接至相应的接线盒内。买方负责提供 2 路电源至油站电控（柜），卖方采用双路电源切换装置负责 2 路电源在油站电控箱内自动切换。卖方应确保双路电源切换装置远方、就地切换时不得引起油泵停运，电源切换后油泵维持原来运行状态。柜内元器件：ABB（中国）有限公司、西门子（中国）有限公司、施耐德电气（中国）有限公司。卖方按上述厂家分别报价，以最高价计入总价，最终由买方确定。\n" +
                "刘韵，\n" +
                "68 \n" +
                "5x \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂2×660MW煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "2.7.3.2 买方负责为电控箱（柜）提供二路动力电源，设计分界点在电控箱（柜）的电源进线端子处。卖方负责设计电控箱（柜）至就地成套设备间的电缆。卖方向买方提供箱（柜）至就地成套设备间电缆清册，由买方确认。\n" +
                "电缆供货分工：电控箱（柜）至就地成套设备间的电缆由卖方负责供货，买方配电设备至动力控制箱（柜）的电源电缆由买方负责供货。\n" +
                "## 2.7.3.3 电控箱数据表\n" +
                "<table><tr><td>序号</td><td>名称</td><td>容量(kW)</td><td>电流(A)</td><td>电压(V)</td><td>电源回路数</td><td>备注</td></tr><tr><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr></table>\n" +
                "## 2.7.3.3 电机电数据表\n" +
                "<table><tr><td>名称</td><td>型号</td><td>额定功率(KW)</td><td>供货数量</td><td>额定电流(A)</td><td>绝缘等级</td><td>防护等级</td><td>制造厂家</td></tr><tr><td>201号甲乙带</td><td>YXKK355-4</td><td>280</td><td>2</td><td>21</td><td>F/VPI</td><td>IP55</td><td>上海电气集团上海电机厂有限公司、佳木斯电机股份有限公司、湘潭电机股份有限公司</td></tr><tr><td>203号甲乙带</td><td>YXKK400-4</td><td>630</td><td>2</td><td>44</td><td>F/VPI</td><td>IP55</td><td>上海电气集团上海电机厂有限公司、佳木斯电机股份有限公司、湘潭电机股份有限公司</td></tr></table>\n" +
                "2.7.3.4 卖方电控箱（柜）中所供负荷有互为备用负荷时，电控箱（柜）的电源进线为双路供电。当电控箱（柜）接有 I 类负荷时，两路电源自动切换，在两路电源自动切换过程中，保证所供负荷不掉电。两路电源可在电控箱（柜）上手动选择任意一路做为工作或备用电源。\n" +
                "卖方所填电源电压种类在以下几种中选择：a）交流380V三相三线电源；b）交流220V单相电源；c）交流380/220V三相四线电源；d）直流220V动力电源(仅限主厂房区域内)。如卖方设备需其它种类电源，由卖方自行解决。\n" +
                "2.7.3.5 电控箱（柜）根据需要可采用顶部、底部进出电缆方式。电控箱采用不锈钢 304 材质，厚度不小于 2.5mm。其外壳防护等级为 IP65，变频器控制柜防护等级为 IP54。柜门采用单层门防尘措施。控制柜设计考虑元件发热、柜内密封问题，控制柜投入运行后不满足现场使用条件，供货厂家到厂解决。\n" +
                "左\n" +
                "刘翔.\n" +
                "谢\n" +
                "69 \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂 $2 \\times 660\\mathrm{MW}$ 煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "2.7.3.6 所有超过 50 平方电缆，直接接进线断路器不经过端子。\n" +
                "2.7.3.7 成套提供连接电缆材质要求铜且符合国标及设计使用标准。\n" +
                "2.7.3.8 成套动力、控制柜本体门内侧要求出厂粘贴塑封图纸一份。\n" +
                "2.7.3.9 动力接线端子每回路预留备用量 1 片，控制接线端子预留 15% 备用量或最少 5 个端子片。端子排使用魏德米勒电联接(上海)有限公司、菲尼克斯（中国）投资有限公司产品。卖方按上述厂家分别报价，以最高价计入总价，最终由买方确定。\n" +
                "2.7.3.10 自动切换开关选用美国 ASCO 或 ME 美登思电气(上海)有限公司自动切换开关。卖方按上述厂家分别报价，以最高价计入总价，最终由买方确定。\n" +
                "2.7.3.11 电气设备二维码标签: 卖方须按照设备整机的二维码, 定制 304 不锈钢二维码标牌 (激光打码), 在主设备合适的位置固定此标签, 随设备一起交付。设备验收时, 此二维码标牌为验收项。二维码功能包含不限于此: 合同编号, 供货清单 (出厂时提供)。2.7.3.12 成套动力、控制柜要求定制 304 不锈钢二维码标牌 (激光打码), 二维码标牌固定位置应遵循现场巡检时便于看到及用移动终端便于扫描为原则, 具体固定位置由买方确定; 具体标识牌尺寸在项目执行过程中确定。二维码功能包含但不限于此: 出厂试验报告、电气图纸, 元件说明书, 设备参数信息, 元器件型号信息、其他相关资料 (出厂时提供)。\n" +
                "2.7.3.13 电气成套供货连接电缆及安装材料包括不限于此：桥架、穿线管、接地线、线管接头、等满足现场安装需求。\n" +
                "## 配套控制柜供货清单\n" +
                "<table><tr><td>名称</td><td>型号</td><td>单位</td><td>数量</td><td>产地</td><td>厂家名称</td></tr><tr><td>XX--控制箱</td><td></td><td></td><td></td><td></td><td>柜内元器件:ABB(中国)有限公司、西门子(中国)有限公司、施耐德电气(中国)有限公司。端子排:魏德米勒电联接(上海)有限公司、菲尼克斯(中国)投资有限公司。</td></tr></table>\n" +
                "## 2.7.4. 起吊装置\n" +
                "每台电动机应装有起吊环、起吊钩或其它便于安全起吊电动机的装置。\n" +
                "## 2.7.5. 铭牌\n" +
                "2.7.5.1. 电动机铭牌上的标注内容应符合所列标准的要求，字样、符号应清晰耐久，铭牌采用不锈钢304材质制作，符合耐腐蚀的要求。\n" +
                "2.7.5.2. 在电动机正常运行时，其铭牌的安装位置应明显可见。\n" +
                "![image](https://cdn-mineru.openxlab.org.cn/result/2026-07-30/e8e8ada6-bb98-44be-aca3-a2b2ab04693e/8df015f0df3062c2620df69c7beeab8b27de9466ce06fb8c689d376ebf6c444b.jpg)\n" +
                "\n" +
                "5x \n" +
                "70 \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂 $2 \\times 660\\mathrm{MW}$ 煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "2.7.5.3. 在单独的铭牌和电动机外形图上还应列出电动机起动的限制条件。\n" +
                "2.7.5.4. 如果使用了耐磨轴承，则应在铭牌上标明耐磨轴承应用标准的编号，\n" +
                "3. 高压电动机供货范围\n" +
                "3.1. 卖方应提供电动机本体及其附件，\n" +
                "3.2. 每台电动机的供货范围应包括下列设备，但不限于此。\n" +
                "3.2.1. 电动机底座及地脚螺栓；\n" +
                "3.2.2. 通风电机、法兰及其接口；\n" +
                "3.2.3. 内部水管路及其进出口接头；（当采用水冷却时）\n" +
                "3.2.4. 通风格栅和过滤器；（当采用管道通风冷却时）\n" +
                "3.2.5. 空间加热器；\n" +
                "3.2.6. 轴承和轴承座；\n" +
                "3.2.7. 联轴器（由卖方成套供货）\n" +
                "3.2.8. 接线盒；\n" +
                "3.2.9. 接地端子；\n" +
                "3.2.10. 起吊钩或起吊环、起吊螺栓；\n" +
                "3.2.11. 定子绕组电阻式温度探测器（RTD）选用重庆川仪、江苏杰创、浙江伦特；\n" +
                "3.2.12. 轴承温度热电阻；\n" +
                "3.2.13. 噪声抑制器（当噪声超过国标时）；\n" +
                "3.2.14. 差动保护用电流互感器及其外罩（2000kW及以上电动机）；\n" +
                "3.2.15. 润滑装置及其控制设备；\n" +
                "3.2.1 技术服务罩\n" +
                "4.1. 项目管理\n" +
                "合同签定后，卖方应指定负责本工程的项目经理，负责协调卖方在工程全过程的各项工作，如工程进度、设计制造、与辅机厂的协调配合、图纸文件、制造确认、包装运输、现场安装、调试验收等，\n" +
                "4.2. 技术文件\n" +
                "4.2.1. 卖方应在协议签订后向买方提供一般性资料，如鉴定证书、报价书、典型说明书、总装图和设备主要参数，\n" +
                "4.2.2. 在技术协议签15日内卖方向买方提供以下技术文件2份，同时提供电子版1\n" +
                "刘.郭\n" +
                "承\n" +
                "71 \n" +
                "4. 令\n" +
                "内蒙古汇能长川发电有限公司汇能集团\n" +
                "长滩电厂 2×660MW 煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "份（图纸采用 Autocad2000 绘制，其余文件采用 office2000）：\n" +
                "4.2.2.1. 电动机的外形图、基础图、安装图；\n" +
                "4.2.2.2. 电动机的铭牌参数；\n" +
                "4.2.2.3. 在额定电压和规定的最低起动电压条件下的电动机转速---转矩曲线、电流---时间的加速曲线、安全堵转电流上升和过负荷运行时间曲线；\n" +
                "4.2.2.4. 电动机的运行发热曲线；\n" +
                "4.2.2.5. 电动机转子拆卸的详细说明；\n" +
                "4.2.2.6. 电动机本体测点图；\n" +
                "4.2.2.7. 电动机本体仪表清单，包括一次元件的型号、数量、协议及制造厂；\n" +
                "4.2.2.8. 电动机接线盒布置位置及端子图；\n" +
                "4.2.2.9. 电动机润滑油站控制原理图、联锁要求、控制箱外形图、安装图、端子排图、润滑油站电动机清单（当需要时）；\n" +
                "4.2.2.10. 电动机加热器联锁要求及容量、接线盒接线图；\n" +
                "4.2.2.11. 电动机的总重、运输重量；\n" +
                "4.2.2.12. 电动机安装所必需的外部接口资料；\n" +
                "4.2.3. 设备供货时提供以下资料：\n" +
                "设备的开箱资料，除了上述图纸外，还应包括安装、运行、维护、修理说明书，部件清单，工厂试验报告，产品合格证。\n" +
                "4.3. 现场服务\n" +
                "在设备安装过程中，卖方应派有经验的技术人员长住现场，免费提供现场服务。长住人员协助买方按照标准检查安装质量，处理测试投运过程中出现的问题。卖方应选派有经验的技术人员对安装和运行人员进行免费培训。\n" +
                "5. 买方的工作\n" +
                "5.1. 买方应向卖方提供有特殊要求的设备技术文件。\n" +
                "5.2. 设备安装过程中，买方应向卖方现场派员提供工作和生活的便利条件。\n" +
                "5.3. 设备制造过程中，买方可派员到卖方进行监造和检验，卖方应积极配合。\n" +
                "6. 工作安排\n" +
                "6.1. 根据工作需要可以召开设计联络会或采用其它形式解决设计与制造中的问题。\n" +
                "6.2. 文件交接应有记录，设计联络会应有会议纪要。\n" +
                "6.3. 卖方提供的设备及附件规格、重量或接线有变化时，应及时书面通知买方。\n" +
                "307. \n" +
                "72 \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂2×660MW煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "6.4. 卖方应填写电动机协议表，其格式和内容按表6.1。\n" +
                "\n" +
                "表6.1\n" +
                "\n" +
                "\n" +
                "电动机协议表:\n" +
                "\n" +
                "<table><tr><td>序号</td><td>名称</td><td>买方要求值</td><td>卖方提供值</td></tr><tr><td>1</td><td>被驱动设备名称</td><td></td><td></td></tr><tr><td>2</td><td>电动机型式/型号</td><td></td><td></td></tr><tr><td>3</td><td>安装方式/地点</td><td></td><td></td></tr><tr><td>4</td><td>铭牌功率 (kW)</td><td></td><td></td></tr><tr><td>5</td><td>额定电压/相数/频率 (kV/ /Hz)</td><td></td><td></td></tr><tr><td>6</td><td>额定耐受试验电压 (kV)</td><td></td><td></td></tr><tr><td>7</td><td>额定转速 (r/min)</td><td></td><td></td></tr><tr><td>8</td><td>起动电压/电流 (kV/A)</td><td></td><td></td></tr><tr><td>9</td><td>绝缘等级/绝缘处理方式</td><td></td><td></td></tr><tr><td>10</td><td>运行系数</td><td></td><td></td></tr><tr><td>11</td><td>满载运行时的温升(电阻法测量)(°C)</td><td></td><td></td></tr><tr><td>12</td><td>外壳/通风方式</td><td></td><td></td></tr><tr><td>13</td><td>外壳防护等级</td><td></td><td></td></tr><tr><td rowspan=\"5\">14</td><td>管道通风式电动机:</td><td></td><td></td></tr><tr><td>外接管道最大压力 (MPa)</td><td></td><td></td></tr><tr><td>外接管道最大允许压降(MPa)</td><td></td><td></td></tr><tr><td>最大空气量 (Nm3/s)</td><td></td><td></td></tr><tr><td>最大入口管道尺寸 (mm)</td><td></td><td></td></tr><tr><td rowspan=\"5\">15</td><td>水对空气冷却式电动机:</td><td></td><td></td></tr><tr><td>外接冷却水管路最大允许压力(MPa)</td><td></td><td></td></tr><tr><td>外接冷却水管路最大允许压降(MPa)</td><td></td><td></td></tr><tr><td>最大冷却水水量 (Nm3/s)</td><td></td><td></td></tr><tr><td>冷却水入水管最大尺寸(mm)</td><td></td><td></td></tr><tr><td>16</td><td>传动轴型式</td><td></td><td></td></tr><tr><td>17</td><td>联轴器型式</td><td></td><td></td></tr><tr><td>18</td><td>外置轴承型号</td><td></td><td></td></tr><tr><td>19</td><td>内置轴承型号</td><td></td><td></td></tr><tr><td>20</td><td>导向轴承型号</td><td></td><td></td></tr><tr><td>21</td><td>推力轴承型号</td><td></td><td></td></tr><tr><td>22</td><td>轴承润滑油流量 (m3/s)</td><td></td><td></td></tr><tr><td>23</td><td>CT变比/准确级(2000kW及以上电动机)</td><td></td><td></td></tr><tr><td>24</td><td>旋转方向</td><td></td><td></td></tr><tr><td>25</td><td>电缆/电缆穿线管尺寸 (mm2/mm)</td><td></td><td></td></tr><tr><td>26</td><td>接线盒尺寸 (mm)</td><td></td><td></td></tr><tr><td>27</td><td>滤网隔栅型号规格</td><td></td><td></td></tr><tr><td>28</td><td>过滤器型号</td><td></td><td></td></tr><tr><td>29</td><td>加热器电压/功率/数量 (V/W)</td><td></td><td></td></tr><tr><td>30</td><td>额定电压/最低起动电压下允许的惰转时间(s)</td><td></td><td></td></tr><tr><td>31</td><td>额定电压/最低起动电压下的加速时间(s)</td><td></td><td></td></tr><tr><td>32</td><td>满载电流/堵转电流 (A)</td><td></td><td></td></tr><tr><td>33</td><td>额定电压/最低起动电压下的起动转矩(%)</td><td></td><td></td></tr><tr><td>34</td><td>额定电压/最低起动电压下的制动转矩(%)</td><td></td><td></td></tr><tr><td>35</td><td>额定电压/最低起动电压下的工作转矩(%)</td><td></td><td></td></tr><tr><td rowspan=\"4\">36</td><td>效率/功率因数:</td><td></td><td></td></tr><tr><td>满载</td><td></td><td></td></tr><tr><td>3/4负载</td><td></td><td></td></tr><tr><td>1/2负载</td><td></td><td></td></tr><tr><td>37</td><td>推荐的润滑油型号规格</td><td></td><td></td></tr><tr><td>38</td><td>转子材料</td><td></td><td></td></tr><tr><td>39</td><td>定子RTD型式/型号</td><td></td><td></td></tr><tr><td>40</td><td>轴承RTD型式/型号</td><td></td><td></td></tr><tr><td>41</td><td>声压级(电动机外壳1米远处) dB(A)</td><td></td><td></td></tr><tr><td>42</td><td>制造厂机座型号/编号</td><td></td><td></td></tr></table>\n" +
                "刘翔\n" +
                "-谢\n" +
                "73 \n" +
                "赵\n" +
                "长滩电厂 2×660MW 煤电一体化扩建项目工程\n" +
                "内蒙古汇能长川发电有限公司汇能集团\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "\n" +
                "237. \n" +
                "74 \n" +
                "内蒙古汇能长川发电有限公司汇能集团\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "长滩电厂 2×660MW 煤电一体化扩建项目工程\n" +
                "<table><tr><td>序号</td><td>名称</td><td>买方要求值</td><td>卖方提供值</td></tr><tr><td>43</td><td>外形尺寸/图号(mm)</td><td></td><td></td></tr></table>\n" +
                "## 7. 备品备件及专用工具\n" +
                "卖方若向买方提供必要的备品备件和专用工具，备品备件应是新品，与设备同型号、同工艺。\n" +
                "## 8. 质量保证和试验\n" +
                "## 8.1. 质量保证\n" +
                "8.1.1. 订购的新型产品除应满足本协议书外，卖方还应提供产品的鉴定证书。\n" +
                "8.1.2. 卖方应保证制造过程中的所有工艺、材料、试验等（包括卖方的外购件在内）均应符合本协议书的规定。若买方根据运行经验指定卖方提供某中外购零部件，卖方应积极配合。\n" +
                "8.1.3. 卖方应遵守本协议书中各条款和工作项目的 ISO900 GB/T1900 质量保证体系，该质量保证体系已经过国家认证和正常运转。\n" +
                "## 8.2. 试验\n" +
                "## 8.2.1. 型式试验\n" +
                "## 8.2.1.1. 温升试验\n" +
                "按 GB755《旋转电机 定额和性能》及 GB1032《三相异步电机试验方法》中有关规定进行。\n" +
                "## 8.2.1.2. 耐压试验（包括匝间冲击耐压试验）\n" +
                "按 GB755《旋转电机 定额和性能》及 GB1032《三相异步电机试验方法》中有关规定进行。\n" +
                "## 8.2.1.3. 空载试验\n" +
                "按 GB1032《三相异步电机试验方法》中有关规定进行。\n" +
                "## 8.2.1.4. 效率、功率因数及转差率的测定试验\n" +
                "按 GB1032《三相异步电机试验方法》中有关规定进行。\n" +
                "## 8.2.1.5. 超速试验\n" +
                "按 GB1032《三相异步电机试验方法》中有关规定进行。\n" +
                "## 8.2.2. 特殊试验\n" +
                "## 8.2.2.1. 堵转试验（仅对鼠笼式电动机）\n" +
                "按 GB1032《三相异步电机试验方法》中有关规定进行。\n" +
                "f(x) \n" +
                "刘一勋.\n" +
                "[Signature] \n" +
                "75 \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂 $2 \\times 660\\mathrm{MW}$ 煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "## 8.2.2.2. 振动的测定试验\n" +
                "按 GB10068《旋转电机振动测定方法及限值》及 GB1032《三相异步电机试验方法》中有关规定进行。\n" +
                "## 8.2.2.3. 噪声的测定试验\n" +
                "按 GB10069《旋转电机噪声测定方法及限值》及 GB1032《三相异步电机试验方法》中有关规定进行。\n" +
                "## 8.2.2.4. 转动惯量的测定试验\n" +
                "按 GB1032《三相异步电机试验方法》中有关规定进行。\n" +
                "8.2.3. 出厂试验（例行试验）\n" +
                "8.2.3.1. 外观检查；\n" +
                "8.2.3.2. 绕组电阻测量;\n" +
                "8.2.3.3. 绝缘电阻测量；\n" +
                "8.2.3.4. 工频绝缘试验；\n" +
                "8.2.3.5. 空载试验;\n" +
                "8.2.3.6. 转子锁紧试验；\n" +
                "8.2.3.7. 振动测量\n" +
                "8.2.4. 现场试验\n" +
                "8.2.4.1. 绕组绝缘电阻、直流电阻测量；\n" +
                "8.2.4.2. 绕组极化率测量；\n" +
                "8.2.4.3. 交直流耐压试验；\n" +
                "8.2.4.4. 相序指示检查；\n" +
                "8.2.4.5. 轴/台板绝缘试验（在适用处）；\n" +
                "8.2.4.6. 其他附件的电气试验\n" +
                "9. 包装、运输和储存\n" +
                "9.1. 设备制造完成并通过试验后应及时包装，否则应得到切实的保护，确保其不受污损。其包装应符合铁路、公路和海运部门的有关规定。\n" +
                "9.2. 所有部件经妥善包装或装箱后，在运输过程中应采取其它防护措施，以免散失损坏或被盗。\n" +
                "9.3. 在包装箱外应标明买方的订货号、发货号。\n" +
                "9.4. 各种包装应能确保各零部件在运输过程中不致遭到损坏、丢失、变形、受潮和腐\n" +
                "![image](https://cdn-mineru.openxlab.org.cn/result/2026-07-30/e8e8ada6-bb98-44be-aca3-a2b2ab04693e/a8f31f0e562a1cc1131ac579aefde02c1506ac60c690a8b5d2c736bff535749a.jpg)\n" +
                "\n" +
                "76 \n" +
                "内蒙古汇能长川发电有限公司汇能集团  \n" +
                "长滩电厂2×660MW煤电一体化扩建项目工程\n" +
                "带式输送机及头部伸缩装置技术协议书\n" +
                "蚀。\n" +
                "9.5. 包装箱上应有明显的储运图示标志。\n" +
                "9.6. 整体产品或分别运输的部件都要符合运输和装载的要求，并能承受在铁路和公路上可能经受的最大冲击力。\n" +
                "9.7. 随产品提供的技术资料应完整无缺。\n" +
                "## 附录A\n" +
                "## 发电厂各类场所电动机外壳防护等级\n" +
                "(参考件)\n" +
                "<table><tr><td rowspan=\"2\">序号</td><td rowspan=\"2\">电动机布置位置</td><td colspan=\"2\">电动机外壳</td></tr><tr><td>型式</td><td>防护等级</td></tr><tr><td>1</td><td>汽机房零米层、半层(第二层)</td><td>TE</td><td>IP43及以上</td></tr><tr><td>2</td><td>汽机房运转层</td><td>--</td><td>IP23及以上</td></tr><tr><td>3</td><td>除氧框架除氧层、锅炉房运转层</td><td>--</td><td>IP54</td></tr><tr><td>4</td><td>锅炉房零米层、空预器辅机、除氧框架零米层、煤仓层</td><td>TE</td><td>IP54</td></tr><tr><td>5</td><td>炉后引风机、户外电动机</td><td>TE</td><td>IP55</td></tr><tr><td>6</td><td>输煤转运站、碎煤机室、输煤栈桥、卸煤沟</td><td>TE</td><td>IP54</td></tr><tr><td>7</td><td>户外煤场、灰场、灰库</td><td>TE</td><td>IP54</td></tr><tr><td>8</td><td>除灰风机房、渣浆泵房、</td><td>TE</td><td>IP55</td></tr><tr><td>9</td><td>锅炉补给水处理室</td><td>TE</td><td>IP54</td></tr><tr><td>10</td><td>酸碱贮存间</td><td>TE *</td><td>IP54</td></tr><tr><td>11</td><td>燃油泵房</td><td>TE **</td><td>IP54</td></tr><tr><td>12</td><td>污水泵房</td><td>TE</td><td>IP54</td></tr><tr><td>13</td><td>潜水泵</td><td>TE</td><td>IP68</td></tr></table>\n" +
                "说明：\n" +
                "[Signature] \n" +
                "刘鹏.\n" +
                "77";

        return message;
    }

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

    @Tool(description = "获取PLM系统所有页面的链接")
    public String getPlmPageUrl() {
        try {
            return objectMapper.writeValueAsString(plmPages);
        } catch (Exception e) {
            return "序列化页面链接失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统查询配置管理模块的任务列表，支持分页查询")
    public String retrieveTaskList(
            @ToolParam(description = "页码，从1开始") int pageNum,
            @ToolParam(description = "每页条数") int pageCount) {

        System.out.println("[🔨] 查询任务列表，pageNum=" + pageNum + ", pageCount=" + pageCount);

        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/qyplmapi/udscfg-plm/task/retrieveTaskList")
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

    @Tool(description = "从PLM系统获取AI计价模块的产品列表，支持分页")
    public String retrieveProductList(
            @ToolParam(description = "页码，从1开始") int pageIndex,
            @ToolParam(description = "每页条数") int pageSize) {

        System.out.println("[🔨] 查询产品列表，pageIndex=" + pageIndex + ", pageSize=" + pageSize);

        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/qyplmapi/-param/api/Product")
                    .queryParam("searchText", "")
                    .queryParam("groupCodes", "")
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
            String configUrl = UriComponentsBuilder.fromHttpUrl(baseUrl + "/qyplmapi/-param/api/ConfigBank")
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
                            baseUrl + "/qyplmapi/-param/api/ConfigBank/" + versionId + "/Param")
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
            String url = baseUrl + "/qyplmapi/accessbusiness/item/instance/action/listbatch/page";

            String requestBody = String.format(
                    "{\"pageNum\":1,\"pageSize\":10,\"searchRevisionTypeEnum\":\"ISLATESTONLY\",\"isSearchChildren\":false,\"orderAttributes\":[],\"objectEntries\":[],\"orSegmentGroup\":{\"andSegmentGroups\":[{\"generalSegments\":[{\"operationKey\":\"LIKE\",\"attribute\":\"objectName\",\"value\":\"%s\"}],\"operationKey\":\"AND\"}],\"operationKey\":\"OR\"}}",
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
            String simplified = simplifyObjectResult(result);
            System.out.println("[✅] 对象信息查询成功");
            return simplified;

        } catch (Exception e) {
            System.err.println("[❌] 查询对象信息失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统根据文件夹名称查询文件夹列表")
    public String retrieveFolderByName(
            @ToolParam(description = "文件夹名称，支持模糊查询") String folderName) {

        System.out.println("[🔨] 查询文件夹信息，folderName=" + folderName);

        try {
            String url = baseUrl + "/qyplmapi/accessbusiness/item/instance/action/listbatch/page";

            String requestBody = String.format(
                    "{\"pageNum\":1,\"pageSize\":10,\"searchRevisionTypeEnum\":\"ISLATESTONLY\",\"isSearchChildren\":false,\"orSegmentGroup\":{\"andSegmentGroups\":[{\"generalSegments\":[{\"operationKey\":\"LIKE\",\"attribute\":\"objectName\",\"value\":\"%s\"}],\"operationKey\":\"AND\"}],\"operationKey\":\"OR\"}}",
                    folderName != null ? folderName.replace("\\", "\\\\").replace("\"", "\\\"") : ""
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
            String simplified = simplifyFolderResult(result);
            System.out.println("[✅] 文件夹信息查询成功");
            return simplified;

        } catch (Exception e) {
            System.err.println("[❌] 查询文件夹信息失败: " + e.getMessage());
            return "查询失败: " + e.getMessage();
        }
    }

    @Tool(description = "从PLM系统删除对象实例")
    public String deleteObject(
            @ToolParam(description = "对象GUID") String guid,
            @ToolParam(description = "业务对象GUID，对应bizObjectGuid") String businessObjectGuid) {

        System.out.println("[🔨] 删除对象，guid=" + guid + ", businessObjectGuid=" + businessObjectGuid);

        try {
            String url = baseUrl + "/qyplmapi/accessbusiness/item/instance";

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

    @Tool(description = "将对象挂载到指定文件夹下")
    public String linkObjectToFolder(
            @ToolParam(description = "文件夹GUID") String folderGuid,
            @ToolParam(description = "文件夹业务对象GUID(bizObjectGuid)") String folderBusinessObjectGuid,
            @ToolParam(description = "对象GUID") String objectGuid,
            @ToolParam(description = "对象业务对象GUID(bizObjectGuid)") String objectBusinessObjectGuid) {

        System.out.println("[🔨] 挂载对象到文件夹，folderGuid=" + folderGuid + ", objectGuid=" + objectGuid);

        try {
            String url = baseUrl + "/qyplmapi/enterprisefolder/folder/linkBatch";

            String requestBody = String.format(
                    "{\"folderObject\":{\"bizObjectGuid\":\"%s\",\"guid\":\"%s\"},\"objectGuidList\":[{\"bizObjectGuid\":\"%s\",\"guid\":\"%s\"}]}",
                    folderBusinessObjectGuid != null ? folderBusinessObjectGuid.replace("\\", "\\\\").replace("\"", "\\\"") : "",
                    folderGuid != null ? folderGuid.replace("\\", "\\\\").replace("\"", "\\\"") : "",
                    objectBusinessObjectGuid != null ? objectBusinessObjectGuid.replace("\\", "\\\\").replace("\"", "\\\"") : "",
                    objectGuid != null ? objectGuid.replace("\\", "\\\\").replace("\"", "\\\"") : ""
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
            System.out.println("[✅] 对象挂载成功");
            return result;

        } catch (Exception e) {
            System.err.println("[❌] 挂载对象失败: " + e.getMessage());
            return "挂载失败: " + e.getMessage();
        }
    }

    @Tool(description = "刷新PLM系统的登录token")
    public String refreshAuthToken() {
        System.out.println("[🔨] 开始刷新PLM登录token");

        try {
            String url = baseUrl + "/qyplmapi/permission/user/login";
            String requestBody = "{\"userName\":\"ren.jiang\",\"password\":\"Uds88888\"}";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String result = response.getBody();
            String newToken = extractToken(result);
            if (newToken != null) {
                this.authToken = newToken;
                System.out.println("[✅] PLM登录token刷新成功");
                return "token刷新成功";
            } else {
                System.err.println("[❌] 从响应中未解析到token");
                return "token刷新失败：未解析到token";
            }

        } catch (Exception e) {
            System.err.println("[❌] 刷新token失败: " + e.getMessage());
            return "token刷新失败: " + e.getMessage();
        }
    }

    /**
     * 简化文件夹查询结果，只保留关键字段
     */
    private String simplifyFolderResult(String originalJson) {
        if (originalJson == null || originalJson.isEmpty()) {
            return originalJson;
        }
        try {
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(originalJson);
            com.fasterxml.jackson.databind.JsonNode data = root.get("data");
            if (data == null || !data.has("list")) {
                return originalJson;
            }
            com.fasterxml.jackson.databind.JsonNode list = data.get("list");
            java.util.List<java.util.Map<String, Object>> simplifiedList = new java.util.ArrayList<>();
            for (com.fasterxml.jackson.databind.JsonNode item : list) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("guid", getText(item, "guid"));
                map.put("businessObjectGuid", getText(item, "businessObjectGuid"));
                map.put("objectName", getText(item, "objectName"));
                map.put("folderType", getText(item, "folderType"));
                map.put("status", getText(item, "status"));
                map.put("ownerUser", getText(item, "ownerUser"));
                map.put("ownerGroup", getText(item, "ownerGroup"));
                map.put("description", getText(item, "description"));
                map.put("createUser", getText(item, "createUser"));
                map.put("createTime", getText(item, "createTime"));
                map.put("updateUser", getText(item, "updateUser"));
                map.put("updateTime", getText(item, "updateTime"));
                simplifiedList.add(map);
            }
            java.util.Map<String, Object> resultData = new java.util.HashMap<>();
            resultData.put("total", getLong(data, "total"));
            resultData.put("list", simplifiedList);
            resultData.put("pageNum", getInt(data, "pageNum"));
            resultData.put("pageSize", getInt(data, "pageSize"));
            resultData.put("pages", getInt(data, "pages"));
            resultData.put("size", getInt(data, "size"));

            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("code", getText(root, "code"));
            result.put("msg", getText(root, "msg"));
            result.put("data", resultData);
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            System.err.println("[❌] 简化文件夹结果失败: " + e.getMessage());
            return originalJson;
        }
    }

    /**
     * 简化对象查询结果，只保留关键字段
     */
    private String simplifyObjectResult(String originalJson) {
        if (originalJson == null || originalJson.isEmpty()) {
            return originalJson;
        }
        try {
            com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(originalJson);
            com.fasterxml.jackson.databind.JsonNode data = root.get("data");
            if (data == null || !data.has("list")) {
                return originalJson;
            }
            com.fasterxml.jackson.databind.JsonNode list = data.get("list");
            java.util.List<java.util.Map<String, Object>> simplifiedList = new java.util.ArrayList<>();
            for (com.fasterxml.jackson.databind.JsonNode item : list) {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("itemId", getText(item, "itemId"));
                map.put("revisionId", getText(item, "revisionId"));
                map.put("objectName", getText(item, "objectName"));
                map.put("fullName", getText(item, "fullName"));
                map.put("status", getText(item, "status$displayString"));
                map.put("ownerUser", getText(item, "ownerUser"));
                map.put("ownerGroup", getText(item, "ownerGroup"));
                map.put("createUser", getText(item, "createUser"));
                map.put("updateUser", getText(item, "updateUser"));
                map.put("createTime", getText(item, "createTime"));
                map.put("updateTime", getText(item, "updateTime"));
                map.put("type", getText(item, "businessObjectName$displayString"));
                map.put("hasBOM", getBoolean(item, "hasBOM"));
                map.put("hasSourceFile", getBoolean(item, "hasSourceFile"));

                String guid = getText(item, "guid");
                String businessObjectGuid = getText(item, "businessObjectGuid");
                String objName = getText(item, "objectName");
                map.put("objectName", String.format("[%s](object=%s/%s/%s)", objName, guid, businessObjectGuid, objName));

                simplifiedList.add(map);
            }
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("code", getText(root, "code"));
            result.put("msg", getText(root, "msg"));
            result.put("data", simplifiedList);
            return objectMapper.writeValueAsString(result) + "\n\n ## 注意事项：\n objectName字段是超链接的格式，回答用户时一定要保证超链接的格式，因为用户需要直接点击跳转";
        } catch (Exception e) {
            System.err.println("[❌] 简化结果失败: " + e.getMessage());
            return originalJson;
        }
    }

    private String getText(com.fasterxml.jackson.databind.JsonNode node, String fieldName) {
        com.fasterxml.jackson.databind.JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asText() : null;
    }

    private boolean getBoolean(com.fasterxml.jackson.databind.JsonNode node, String fieldName) {
        com.fasterxml.jackson.databind.JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() && field.asBoolean();
    }

    private long getLong(com.fasterxml.jackson.databind.JsonNode node, String fieldName) {
        com.fasterxml.jackson.databind.JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asLong() : 0L;
    }

    private int getInt(com.fasterxml.jackson.databind.JsonNode node, String fieldName) {
        com.fasterxml.jackson.databind.JsonNode field = node.get(fieldName);
        return field != null && !field.isNull() ? field.asInt() : 0;
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

    /**
     * 从登录响应中提取token
     */
    private String extractToken(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            String tokenKey = "\"token\"";
            int tokenIndex = json.indexOf(tokenKey);
            if (tokenIndex == -1) {
                return null;
            }
            int colonIndex = json.indexOf(":", tokenIndex + tokenKey.length());
            if (colonIndex == -1) {
                return null;
            }
            int start = json.indexOf("\"", colonIndex + 1);
            if (start == -1) {
                return null;
            }
            int end = json.indexOf("\"", start + 1);
            if (end != -1) {
                return json.substring(start + 1, end);
            }
        } catch (Exception e) {
            System.err.println("解析token失败: " + e.getMessage());
        }
        return null;
    }
}
