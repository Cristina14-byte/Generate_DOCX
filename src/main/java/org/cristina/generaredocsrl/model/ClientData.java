package org.cristina.generaredocsrl.model;

import java.util.ArrayList;
import java.util.List;

public class ClientData {
    private int personId;
    private String clientSrl;
    private String personalSrl;
    private String contractNr;
    private int price;
    private String activityDomain;
    private String hq;
    private String cui;
    private String rcRegister;
    private String administrator;
    private boolean psi;
    private boolean ssm;
    private List<String> selectedJobs = new ArrayList<String>();
    private String psiResponsible;

    public String getClientSrl() {
        return clientSrl;
    }

    public void setClientSrl(String clientSrl) {
        this.clientSrl = clientSrl;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getPersonalSrl() {
        return personalSrl;
    }

    public void setPersonalSrl(String usedSrl) {
        this.personalSrl = usedSrl;
    }

    public String getContractNr() {
        return contractNr;
    }

    public void setContractNr(String contractNr) {
        this.contractNr = contractNr;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getActivityDomain() {
        return activityDomain;
    }

    public void setActivityDomain(String activityDomain) {
        this.activityDomain = activityDomain;
    }

    public String getHq() {
        return hq;
    }

    public void setHq(String hq) {
        this.hq = hq;
    }

    public String getCui() {
        return cui;
    }

    public void setCui(String cui) {
        this.cui = cui;
    }

    public String getRcRegister() {
        return rcRegister;
    }

    public void setRcRegister(String rcRegister) {
        this.rcRegister = rcRegister;
    }

    public String getAdministrator() {
        return administrator;
    }

    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    public boolean isPsi() {
        return psi;
    }

    public void setPsi(boolean psi) {
        this.psi = psi;
    }

    public boolean isSsm() {
        return ssm;
    }

    public void setSsm(boolean ssm) {
        this.ssm = ssm;
    }

    public List<String> getSelectedJobs() {
        return selectedJobs;
    }

    public void setSelectedJobs(List<String> selectedJobs) {
        this.selectedJobs = selectedJobs;
    }

    public String getPsiResponsible() {
        return psiResponsible;
    }

    public void setPsiResponsible(String psiResponsible) {
        this.psiResponsible = psiResponsible;
    }
}

