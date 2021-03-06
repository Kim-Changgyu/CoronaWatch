package com.team12.coronawatch;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GraphFragment extends Fragment {
    Spinner spinner;
    BarChart defBarChart, examBarChart, clearBarChart, deathBarChart,
            defIncBarChart, examIncBarChart, clearIncBarChart, deathIncBarChart;
    ArrayList<BarChart> bcList;
    ArrayAdapter<?> aa_items;

    public GraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph, container, false);
        spinner = v.findViewById(R.id.spinner);
        defBarChart = v.findViewById(R.id.defBarChart);
        defIncBarChart = v.findViewById(R.id.defIncBarChart);
        examBarChart = v.findViewById(R.id.examBarChart);
        examIncBarChart = v.findViewById(R.id.examIncBarChart);
        clearBarChart = v.findViewById(R.id.clearBarChart);
        clearIncBarChart = v.findViewById(R.id.clearIncBarChart);
        deathBarChart = v.findViewById(R.id.deathBarChart);
        deathIncBarChart = v.findViewById(R.id.deathIncBarChart);

        String[] items = getResources().getStringArray(R.array.graph_tab);
        aa_items = new ArrayAdapter<>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, items);
        spinner.setAdapter(aa_items);

        bcList = new ArrayList<>();
        bcList.add(defBarChart);
        bcList.add(defIncBarChart);
        bcList.add(examBarChart);
        bcList.add(examIncBarChart);
        bcList.add(clearBarChart);
        bcList.add(clearIncBarChart);
        bcList.add(deathBarChart);
        bcList.add(deathIncBarChart);

        ArrayList<BarEntry> defCntBE = new ArrayList<>();
        ArrayList<BarEntry> defIncCntBE = new ArrayList<>();
        ArrayList<BarEntry> examCntBE = new ArrayList<>();
        ArrayList<BarEntry> examIncCntBE = new ArrayList<>();
        ArrayList<BarEntry> clearCntBE = new ArrayList<>();
        ArrayList<BarEntry> clearIncCntBE = new ArrayList<>();
        ArrayList<BarEntry> deathCntBE = new ArrayList<>();
        ArrayList<BarEntry> deathIncCntBE = new ArrayList<>();

        ArrayList<String> createDtList = CoronaKoreaStatus.createDtList;
        ArrayList<Long> defCntList = CoronaKoreaStatus.decideCntList;
        ArrayList<Long> defIncCntList = CoronaKoreaStatus.decideIncCntList;
        ArrayList<Long> examCntList = CoronaKoreaStatus.examCntList;
        ArrayList<Long> examIncCntList = CoronaKoreaStatus.examIncCntList;
        ArrayList<Long> clearCntList = CoronaKoreaStatus.clearCntList;
        ArrayList<Long> clearIncCntList = CoronaKoreaStatus.clearIncCntList;
        ArrayList<Long> deathCntList = CoronaKoreaStatus.deathCntList;
        ArrayList<Long> deathIncCntList = CoronaKoreaStatus.deathIncCntList;

        for (int i = 0; i < 7; i++) {
            defCntBE.add(
                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                            createDtList.get(i).substring(8, 10)), defCntList.get(i)));
            examCntBE.add(
                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                            createDtList.get(i).substring(8, 10)), examCntList.get(i)));
            clearCntBE.add(
                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                            createDtList.get(i).substring(8, 10)), clearCntList.get(i)));
            deathCntBE.add(
                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                            createDtList.get(i).substring(8, 10)), deathCntList.get(i)));

        }
        for (int i = 0; i < 6; i++) {
            defIncCntBE.add(
                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                            createDtList.get(i).substring(8, 10)), defIncCntList.get(i)));
            examIncCntBE.add(
                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                            createDtList.get(i).substring(8, 10)), examIncCntList.get(i)));
            clearIncCntBE.add(
                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                            createDtList.get(i).substring(8, 10)), clearIncCntList.get(i)));
            deathIncCntBE.add(
                    new BarEntry(Float.parseFloat("" + createDtList.get(i).substring(5, 7) +
                            createDtList.get(i).substring(8, 10)), deathIncCntList.get(i)));
        }

        BarDataSet defBDS, defIncBDS, examBDS, examIncBDS,
                clearBDS, clearIncBDS, deathBDS, deathIncBDS;
        ArrayList<BarDataSet> bdsList = new ArrayList<>();

        defBDS = new BarDataSet(defCntBE, "????????? ???(???)");
        defIncBDS = new BarDataSet(defIncCntBE, "?????? ?????? ????????? ?????? ???(???)");
        examBDS = new BarDataSet(examCntBE, "???????????? ???(???)");
        examIncBDS = new BarDataSet(examIncCntBE, "?????? ?????? ???????????? ?????? ???(???)");
        clearBDS = new BarDataSet(clearCntBE, "???????????? ???(???)");
        clearIncBDS = new BarDataSet(clearIncCntBE, "?????? ?????? ???????????? ?????? ???(???)");
        deathBDS = new BarDataSet(deathCntBE, "????????? ???(???)");
        deathIncBDS = new BarDataSet(deathIncCntBE, "?????? ?????? ????????? ?????? ???(???)");

        bdsList.add(defBDS);
        bdsList.add(defIncBDS);
        bdsList.add(examBDS);
        bdsList.add(examIncBDS);
        bdsList.add(clearBDS);
        bdsList.add(clearIncBDS);
        bdsList.add(deathBDS);
        bdsList.add(deathIncBDS);

        for (BarDataSet bds : bdsList) {
            bds.setColor(Color.parseColor("#CCE65100"));
            bds.setValueTextColor(Color.BLACK);
            bds.setValueTextSize(10f);
            bds.setValueTypeface(Typeface.DEFAULT_BOLD);
            bds.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    DecimalFormat formatter = new DecimalFormat("###,###,##0");
                    return formatter.format(value);
                }
            });
        }

        BarData defBD, defIncBD, examBD, examIncBD,
                clearBD, clearIncBD, deathBD, deathIncBD;

        defBD = new BarData(defBDS);
        defIncBD = new BarData(defIncBDS);
        examBD = new BarData(examBDS);
        examIncBD = new BarData(examIncBDS);
        clearBD = new BarData(clearBDS);
        clearIncBD = new BarData(clearIncBDS);
        deathBD = new BarData(deathBDS);
        deathIncBD = new BarData(deathIncBDS);

        ArrayList<BarData> bdList = new ArrayList<>();
        bdList.add(defBD);
        bdList.add(defIncBD);
        bdList.add(examBD);
        bdList.add(examIncBD);
        bdList.add(clearBD);
        bdList.add(clearIncBD);
        bdList.add(deathBD);
        bdList.add(deathIncBD);

        ArrayList<String> desList = new ArrayList<>();
        desList.add("????????? ???");
        desList.add("????????? ??????");
        desList.add("???????????? ???");
        desList.add("???????????? ??????");
        desList.add("???????????? ???");
        desList.add("???????????? ??????");
        desList.add("????????? ???");
        desList.add("????????? ??????");

        int i = 0;
        for (BarChart bc : bcList) {
            bc.setFitBars(true);
            bc.setData(bdList.get(i));
            bc.setTouchEnabled(false);
            bc.getDescription().setText("???????????? " + desList.get(i++) + " ?????????");
            bc.getDescription().setTextSize(11f);
            bc.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            bc.getXAxis().setTextSize(10f);
            bc.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    String s = Float.toString(value);
                    return s.substring(0, 2) + "/" + s.substring(2, 4);
                }
            });
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (BarChart bc : bcList)
                    bc.setVisibility(View.INVISIBLE);
                bcList.get(position).setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            // Inflate the layout for this fragment
        });
    }
}
