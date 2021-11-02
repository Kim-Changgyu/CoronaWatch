package com.team12.coronawatch;
/*
    @title 공공데이터포털_보건복지부_코로나_시도별발생현황_API_사용_샘플코드
    @author 윤낙원
    @date 2020-11-19
 */

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

class RegionInfo implements Comparable<RegionInfo> {
    public String gubun;       //시도명_한글
    public String gubunEn;     //시도명_영문

    public long defCnt;        //확진자 수
    public long isolClearCnt;  //격리해제 수
    public long isolIngCnt;    //격리중 환자수
    public long deathCnt;      //사망자 수

    public int incDec;         //전일 대비 증감 수
    public String createDt;    //등록일시
    public String updateDt;    //수정일시

    public String getGubun() {
        return gubun;
    }

    public void setGubun(String gubun) {
        this.gubun = gubun;
    }

    public String getGubunEn() {
        return gubunEn;
    }

    public void setGubunEn(String gubunEn) {
        this.gubunEn = gubunEn;
    }

    public long getDefCnt() {
        return defCnt;
    }

    public void setDefCnt(long defCnt) {
        this.defCnt = defCnt;
    }

    public long getIsolClearCnt() {
        return isolClearCnt;
    }

    public void setIsolClearCnt(long isolClearCnt) {
        this.isolClearCnt = isolClearCnt;
    }

    public long getIsolIngCnt() {
        return isolIngCnt;
    }

    public void setIsolIngCnt(long isolIngCnt) {
        this.isolIngCnt = isolIngCnt;
    }

    public long getDeathCnt() {
        return deathCnt;
    }

    public void setDeathCnt(long deathCnt) {
        this.deathCnt = deathCnt;
    }

    public int getIncDec() {
        return incDec;
    }

    public void setIncDec(int incDec) {
        this.incDec = incDec;
    }

    public String getCreateDt() {
        return createDt;
    }

    public void setCreateDt(String createDt) {
        this.createDt = createDt;
    }

    public String getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(String updateDt) {
        this.updateDt = updateDt;
    }

    @Override
    public int compareTo(RegionInfo o) {
        if (this.defCnt < o.getDefCnt()) {
            return -1;
        } else if (this.defCnt > o.getDefCnt()) {
            return 1;
        }
        return 0;
    }
}

class CoronaRegionalStatus {
    //URL 관련 변수
    String urlBuilder;
    String UTF;
    String SERVICE_URL;
    String SERVICE_KEY;

    //포맷 변수
    DecimalFormat formatter;
    SimpleDateFormat dateFormatForComp, dateFormat_year, dateFormat_month, dateFormat_day, dateFormat_hour;

    //날짜 및 시간관련 변수
    Date time;
    String sYear, sMonth, sDay, sHour, sToday, sYesterday, sTwoDayAgo;
    boolean serverTodayFlag;
    String stdYestFromServer, stdTodayFromServer;
    int[] days;
    int nYear, nMonth, nDay, nHour;

    //정보 변수(다른 곳에 활용할때는 이 변수들을 활용하면 됨)
    /*
        gubunList: 시도명(한글)이 시도별로 담겨 있는 리스트
        gubunEnList: 시도명(영문)이 시도별로 담겨 있는 리스트
        defCntList: 시도별 확진자가 저장되어 있는 리스트
        isolIngCntList: 시도별 격리중인 환자 수가 저장되어 있는 리스트
        isolClearCntList: 시도별 격리해제 수가 저장되어 있는 리스트
        deathCntList: 시도별 사망자 수가 저장되어 있는 리스트
        incDecList: 시도별 전일 대비 증감 수가 저장되어 있는 리스트

        createDtList: 등록일시 (ex. 2020-11-19 09:10:13)가 저장되어 있는 리스트
        updateDtList: 업데이트일시 (ex. 2020-11-19 09:10:13)가 저장되어 있는 리스트
     */
    ArrayList<String> gubunList, gubunEnList;
    ArrayList<Long> defCntList, isolClearCntList, isolIngCntList, deathCntList;
    ArrayList<Integer> defIncList;
    ArrayList<String> createDtList, updateDtList;

    //파싱 관련 변수
    Element header, body, items, item;
    String resultCode;
    Node gubun, gubunEn, defCnt, isolClearCnt, isolIngCnt, deathCnt, incDec, createDt, updateDt;
    ArrayList<RegionInfo> regionInfoList;

    CoronaRegionalStatus() {
        init();
    }

    void init() {
        UTF = "UTF-8";
        SERVICE_URL = "http://openapi.data.go.kr/openapi/service/rest/Covid19/" +
                "getCovid19SidoInfStateJson";
        SERVICE_KEY = "=kC3ljqNBvF0D3D0MgwkBdzUlKztg0V2yJ%2BVkvqsymD0dJNuZmK%" +
                "2B3LGpamas7GkxZJM07ADoSl6WR%2BdJODqB7sg%3D%3D";  //보건복지부_코로나19_국내_시_도별_발생현황_일반인증키(UTF-8)

        dateFormatForComp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat_year = new SimpleDateFormat("yyyy", Locale.getDefault());
        dateFormat_month = new SimpleDateFormat("MM", Locale.getDefault());
        dateFormat_day = new SimpleDateFormat("dd", Locale.getDefault());
        dateFormat_hour = new SimpleDateFormat("HH", Locale.getDefault());
        time = new Date();

        formatter = new DecimalFormat("###,###");

        days = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        sYear = dateFormat_year.format(time);
        sMonth = dateFormat_month.format(time);
        sDay = dateFormat_day.format(time);
        sHour = dateFormat_hour.format(time);

        nYear = Integer.parseInt(sYear);
        nMonth = Integer.parseInt(sMonth);
        nDay = Integer.parseInt(sDay);
        nHour = Integer.parseInt(sHour);

        sToday = sYear + '-'
                + sMonth + '-' + sDay;

        sYesterday = dayAgo(1);
        sYesterday = sYesterday.substring(0, 4) + '-'
                + sYesterday.substring(4, 6) + '-' + sYesterday.substring(6, 8);
        sTwoDayAgo = dayAgo(2);
        sTwoDayAgo = sTwoDayAgo.substring(0, 4) + '-'
                + sTwoDayAgo.substring(4, 6) + '-' + sTwoDayAgo.substring(6, 8);

        regionInfoList = new ArrayList<>();

        gubunList = new ArrayList<>();
        gubunEnList = new ArrayList<>();
        defCntList = new ArrayList<>();
        isolIngCntList = new ArrayList<>();
        isolClearCntList = new ArrayList<>();
        deathCntList = new ArrayList<>();
        defIncList = new ArrayList<>();
        createDtList = new ArrayList<>();
        updateDtList = new ArrayList<>();
    }

    public String dayAgo(int subNum) {
        return calDate(nYear, nMonth, nDay, subNum);
    }

    public String calDate(int year, int month, int day, int subNumber) {   //n일 전의 date 반환하는 함수
        String date;

        if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {    //윤년 계산
            days[2] = 29;
        } else {
            days[2] = 28;
        }

        if (subNumber >= day) {
            if (month != 1) {
                day += days[month - 1];
                day -= subNumber;
                month--;
            } else {
                day += days[12];
                day -= subNumber;
                month = 12;
                year--;
            }
        } else {
            day -= subNumber;
        }

        date = Integer.toString(year);

        if (month < 10) {
            date += "0" + month;
        } else {
            date += month;
        }

        if (day < 10) {
            date += "0" + day;
        } else {
            date += day;
        }

        return date;
    }

    protected boolean loadXML() {
        int nWeekAgo = 1,
                nToday = 0;
        serverTodayFlag = true;
        for (int i = 0; i < 2; i++) {
            try {
                urlBuilder = SERVICE_URL + "?" + URLEncoder.encode("ServiceKey", UTF) + SERVICE_KEY + /*Service Key*/
                        "&" + URLEncoder.encode("pageNo", UTF) + "=" + URLEncoder.encode("1", UTF) + /*페이지번호*/
                        "&" + URLEncoder.encode("numOfRows", UTF) + "=" + URLEncoder.encode("10", UTF) + /*한 페이지 결과 수*/
                        "&" + URLEncoder.encode("startCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo(nWeekAgo), UTF) + /*검색할 생성일 범위의 시작*/
                        "&" + URLEncoder.encode("endCreateDt", UTF) + "=" + URLEncoder.encode(dayAgo(nToday), UTF);/*URL*//*검색할 생성일 범위의 종료*/
                if (i == 1) {
                    Log.i("loadXML()", urlBuilder);
                }
            } catch (Exception e) {
                Log.i("loadXML()", e.getMessage());
            }

            Document doc = null;
            try {
                URL url = new URL(urlBuilder);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
            } catch (IOException | SAXException | ParserConfigurationException e) {
                Log.i("CoronaRegClass", e.getMessage());
            }
            if (doc == null) {
                return false;
            }
            header = (Element) doc.getElementsByTagName("header").item(0);
            resultCode = header.getElementsByTagName("resultCode").item(0)
                    .getChildNodes().item(0).getNodeValue();
            if (!CoronaNationalStatus.isParseError(resultCode)) {
                return false;
            }
            body = (Element) doc.getElementsByTagName("body").item(0);
            items = (Element) body.getElementsByTagName("items").item(0);
            item = (Element) items.getElementsByTagName("item").item(0);

            Node tmpCreateDt = item.getElementsByTagName("createDt").item(0);
            String sTmpCreateDt = tmpCreateDt.getChildNodes().item(0).getNodeValue();
            if (i == 0) {
                if (!sTmpCreateDt.substring(0, 10).equals(sToday)) {
                    System.out.println(sTmpCreateDt.substring(0, 10) + "-" + sToday);
                    nWeekAgo = 2;
                    nToday = 1;
                    serverTodayFlag = false;
                    stdYestFromServer = sTwoDayAgo;
                    stdTodayFromServer = sYesterday;
                } else {
                    stdYestFromServer = sYesterday;
                    stdTodayFromServer = sToday;
                    break;
                }
            }
        }
        return true;
    }

    protected void parseXML() {
        loadXML();
        Log.i("parseXML()", stdTodayFromServer);
        Log.i("parseXML()", stdYestFromServer);
        int i = 0;
        while (true) {
            RegionInfo regionInfo = new RegionInfo();
            item = (Element) items.getElementsByTagName("item").item(i++);

            if (item == null) {
                break;
            }

            gubun = item.getElementsByTagName("gubun").item(0);     //시도명(한글)
            gubunEn = item.getElementsByTagName("gubunEn").item(0);     //시도명(영문)
            defCnt = item.getElementsByTagName("defCnt").item(0);       //확진자 수(지역별)
            isolClearCnt = item.getElementsByTagName("isolClearCnt").item(0);       //격리해제 수(지역별)
            isolIngCnt = item.getElementsByTagName("isolIngCnt").item(0);    //격리환자 수(지역별)
            deathCnt = item.getElementsByTagName("deathCnt").item(0);    //사망자 수(지역별)
            incDec = item.getElementsByTagName("incDec").item(0);
            createDt = item.getElementsByTagName("createDt").item(0);    //등록일시
            updateDt = item.getElementsByTagName("updateDt").item(0);    //등록일시

            String sGubun = gubun.getChildNodes().item(0).getNodeValue();
            if (sGubun.equals("합계")) {
                continue;
            }
            regionInfo.setGubun(sGubun);
            regionInfo.setGubunEn(gubunEn.getChildNodes().item(0).getNodeValue());
            regionInfo.setDefCnt(Long.parseLong(defCnt.getChildNodes().item(0).getNodeValue()));
            regionInfo.setIsolClearCnt(Long.parseLong(isolClearCnt.getChildNodes().item(0).getNodeValue()));
            regionInfo.setIsolIngCnt(Long.parseLong(isolIngCnt.getChildNodes().item(0).getNodeValue()));
            regionInfo.setDeathCnt(Long.parseLong(deathCnt.getChildNodes().item(0).getNodeValue()));
            regionInfo.setIncDec(Integer.parseInt(incDec.getChildNodes().item(0).getNodeValue()));

            regionInfo.setCreateDt(createDt.getChildNodes().item(0).getNodeValue());
            String sUpdateDt = updateDt.getChildNodes().item(0).getNodeValue();
            if (sUpdateDt.equals("null")) {
                sUpdateDt = "수정내역 없음";
            }
            regionInfo.setUpdateDt(sUpdateDt);

            regionInfoList.add(regionInfo);
        }
        Collections.sort(regionInfoList.subList(0, regionInfoList.size() - 1));

        for (RegionInfo regionInfo : regionInfoList) {
            Log.i("CoronaRegionalStatus: ", sToday);
            Log.i("CoronaRegionalStatus: ", regionInfo.getCreateDt().substring(0, 10));

            if(!serverTodayFlag) {
                sToday = sYesterday;
            }

            if (regionInfo.getCreateDt().substring(0, 10).equals(sToday)) {
                gubunList.add(regionInfo.getGubun());
                gubunEnList.add(regionInfo.getGubunEn());
                defCntList.add(regionInfo.getDefCnt());
                isolIngCntList.add(regionInfo.getIsolIngCnt());
                isolClearCntList.add(regionInfo.getIsolClearCnt());
                deathCntList.add(regionInfo.getDeathCnt());
                defIncList.add(regionInfo.getIncDec());
                createDtList.add(regionInfo.getCreateDt());
                updateDtList.add(regionInfo.getUpdateDt());
            }
        }
    }
}
