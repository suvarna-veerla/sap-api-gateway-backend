package com.hcl.gateway.model;

public class SapSalesOrder {
    private String VBELN; // SAP Sales Document/Order Number
    private String KUNNR; // SAP Customer Number
    private String MATNR; // SAP Material/Product Number
    private double NETWR; // SAP Net Value / Price
    private String WAERK; // SAP Document Currency (e.g., INR)

    // Constructor
    public SapSalesOrder(String VBELN, String KUNNR, String MATNR, double NETWR, String WAERK) {
        this.VBELN = VBELN;
        this.KUNNR = KUNNR;
        this.MATNR = MATNR;
        this.NETWR = NETWR;
        this.WAERK = WAERK;
    }

    // Getters (స్ప్రింగ్ బూట్ దీన్ని JSON గా మార్చడానికి ఇవి అవసరం)
    public String getVBELN() { return VBELN; }
    public String getKUNNR() { return KUNNR; }
    public String getMATNR() { return MATNR; }
    public double getNETWR() { return NETWR; }
    public String getWAERK() { return WAERK; }
}
