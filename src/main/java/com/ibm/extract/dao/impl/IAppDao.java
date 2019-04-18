package com.ibm.extract.dao.impl;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.ibm.extract.model.App;
import com.ibm.extract.model.Chain;
import com.ibm.extract.model.OrgCode;

public interface IAppDao {
	@Select("select trim(app_code) appCode,trim(name) name from appl.application where app_code in ('FIW_UICAMMC','CDMS','WWBalsht','WWConsol','WWSD')")
	public List<App> getAllApp();
	@Select("select distinct(trim(serial)||trim(cc)) serialCc from req.access_req where status='GRANTED' AND app_code=#{appCode} with ur")
	public List<String> getRequests(@Param("appCode")String appCode);
	//get specific request from app_code
	//FIW_UICAMMC
	@Select("select trim(serial)||trim(cc) from bp.ww_emp where serial||cc in ( select distinct serial||cc from req.access_req where status='GRANTED' and request_id in( select request_id from req.requested_elements where app_code='FIW_UICAMMC' and input_form_field='<strong>*Role</strong>' and input_form_val='Accounting' and request_id in(select request_id from req.requested_elements where app_code='FIW_UICAMMC' and input_form_field='<strong>*Access Type</strong>' and input_form_val='Total IBM')) ) union select serial||cc from bp.ww_emp where serial||cc in ( select distinct serial||cc from req.access_req where status='GRANTED' and request_id in( select request_id from req.requested_elements where app_code='FIW_UICAMMC' and input_form_field='<strong>*Access Type</strong>' and input_form_val='Total IBM - WW Accounting access'))")
	public List<String> getRequestsFromFIW();
	//CDMS
	@Select("select trim(serial)||trim(cc) from bp.ww_emp where trim(serial)||trim(cc) in( select distinct trim(serial)||trim(cc) from req.access_req where status='GRANTED' and app_code='CDMS' and request_id in(  select request_id from PROV.REQ_BLUEGROUPS_TRANSACTIONS where app_code='CDMS' and bluegroup_name in ('External Reporting', 'Global Financing', 'Policy', 'SEC Tech Support') and status='COMPLETE'))")
	public List<String> getRequestsFromCDMS();
	//WWBalsht
	@Select("select trim(serial)||trim(cc) from bp.ww_emp where trim(serial)||trim(cc) in( select distinct(trim(serial)||trim(cc)) from req.access_req where status='GRANTED' and request_id in(  select request_id from PROV.REQ_BLUEGROUPS_TRANSACTIONS where app_code='WWBalsht' and bluegroup_name in ('wwbalsht_blackout_users') and status='COMPLETE') and request_id in (select request_id from PROV.REQ_BLUEGROUPS_TRANSACTIONS where app_code='WWBalsht' and bluegroup_name in ('wwbalsht_ww_all_r','wwbalsht_ww_all_w') and status='COMPLETE')) union select serial||cc from bp.ww_emp where serial||cc in( select distinct serial||cc from req.access_req where status='GRANTED' and request_id in( select request_id from PROV.REQ_BLUEGROUPS_TRANSACTIONS where app_code='WWBalsht' and bluegroup_name in ('wwbalsht_admin','wwbalsht_dba_admin') and status='COMPLETE')) union select serial||cc from bp.ww_emp where serial||cc in ('098500834', 'C-BHES897', '629243897', '028159897', '746273897', '048385834', '050696834', '063003834', '3A2960897', '011801834', '083910834', '129396897')")
	public List<String> getRequestsFromWwBalsht();
	//WWConsol
	@Select("select trim(serial)||trim(cc) from bp.ww_emp where serial||cc in( select distinct serial||cc from req.access_req where status='GRANTED' and request_id in(  select request_id from PROV.REQ_BLUEGROUPS_TRANSACTIONS where app_code='WWConsol' and bluegroup_name in ('wwconsol_blackout_users') and status='COMPLETE') and request_id in (select request_id from PROV.REQ_BLUEGROUPS_TRANSACTIONS where app_code='WWConsol' and bluegroup_name in ('wwconsol_ww_all_r','wwconsol_ww_all_w', 'wwconsol_ww_chn_r','wwconsol_ww_chn_w', 'wwconsol_ww_ind_r','wwconsol_ww_ind_w', 'wwconsol_ww_rev_r','wwconsol_ww_rev_w', 'wwconsol_ww_sd_r','wwconsol_ww_sd_w' ) and status='COMPLETE')) union select serial||cc from bp.ww_emp where serial||cc in( select distinct serial||cc from req.access_req where status='GRANTED' and request_id in( select request_id from PROV.REQ_BLUEGROUPS_TRANSACTIONS where app_code='WWConsol' and bluegroup_name in ('wwconsol_admin','wwconsol_dba_admin') and status='COMPLETE')) union select serial||cc from bp.ww_emp where serial||cc in ('3A2332897', '746273897', '050696834', '083910834', '0J9394897', '129396897')")
	public List<String> getRequestsFromWWConsol();
	//WWSD
	@Select("select trim(serial)||trim(cc) from req.access_req where app_code='WWSD' and status='GRANTED' and trim(serial)||trim(cc) in ('058777897', '3G0363897', '789063897', '5D1788897', '072532613', '4G6388897', '7A7003897', '9D0145897', '5D4452897', '5D2385897', '6G0311897', '1G9560897', '506201897', '2G3419897', '5G8469897', '5D2315897', '5G1244897', '9D9364897', '012496838', '095025706', '235270897', '1J7832897', '5G7582897', '4G6388897', '4G9913897', '012325838', '3J0290897', '0J4319897', '0J2116897', '2A0161897', '1A3816897', '0J9132897', '3G9198897', '7D2945897', '0J4798897', '0J9437897', '2J9319897', '4D7742897', '2J8440897', '392068897')")
	public List<String> getRequestsFromWWSD();
	
	
	@Select("select trim(a.serial)||trim(a.cc) user,trim(a.notesmail) userNotes,trim(a.bp_orgCode) bpOrgCode,trim(a.CALLUPNAME) userName, trim(a.mgrserial)||trim(a.mgrcc) manager,trim(b.notesmail) mgrNotes,trim(b.CALLUPNAME) mgrName, trim(a.FUNCLEADER_SERIAL)||trim(a.FUNCLEADER_CC) funLeader,trim(c.notesmail) funLeaderNotes,trim(c.CALLUPNAME) funLeaderName  from  bp.ww_emp a left join bp.ww_emp b on (trim(a.mgrserial)||trim(a.mgrcc)=trim(b.serial)||trim(b.cc)) left join bp.ww_emp c on (trim(a.FUNCLEADER_SERIAL)||trim(a.FUNCLEADER_CC)=trim(c.serial)||trim(c.cc)) where  trim(a.serial)||trim(a.cc) in (${serialCcs})")//${valueIds}
	public List<Chain> getChain(@Param("serialCcs")String serialCcs);
	@Select("select distinct(trim(serial)||trim(cc)) from bp.ww_emp where trim(serial)||trim(cc)=trim(mgrserial)||trim(mgrcc)")
	public List<String> getSpecialEmp();
	@Select("select trim(serial)||trim(cc) user,trim(mgrserial)||trim(mgrcc) manager,trim(FUNCLEADER_SERIAL)||trim(FUNCLEADER_CC) funLeader  from bp.ww_emp where trim(serial)||trim(cc) in (${serialCcs})")
	public List<Chain> getUpMgrLine(@Param("serialCcs")String serialCcs);
	@Select("select trim(serial)||trim(cc) user,trim(mgrserial)||trim(mgrcc) manager,trim(FUNCLEADER_SERIAL)||trim(FUNCLEADER_CC) funLeader  from bp.ww_emp where trim(serial)||trim(cc) in (${serialCcs})")
	public List<Chain> getUpFunLeaderLine(@Param("serialCcs")String serialCcs);
//	@Select("select trim(serial)||trim(cc) uid,trim(FUNCLEADER_SERIAL)||trim(FUNCLEADER_CC) funLeader  from bp.ww_emp where trim(serial)||trim(cc) in (${serialCcs})")
//	public List<Chain> getBpID(@Param("serialCcs")String serialCcs);
	
	@Select("select distinct(trim(bp_orgCode)) from bp.ww_emp WHERE BP_ORGCODE not in ('','??')")
	public List<String> getBpOrgCode();
	//Sqlite
	@Insert("insert into HR_ORG_CODE(hrOrgCode,hrOrgDisplay,hrUnitId)values('${hrOrgCode}','${hrOrgDisplay}','${hrUnitId}');")
	public int saveInLocalDB(@Param("hrOrgCode")String hrOrgCode,@Param("hrOrgDisplay")String hrOrgDisplay,@Param("hrUnitId")String hrUnitId);
	@Select("select hrOrgCode,hrOrgDisplay,hrUnitId from HR_ORG_CODE")
	public List<OrgCode> getAllOrgCode();
	@Delete("DELETE FROM HR_ORG_CODE")
	public void deleteAllOrgCode();
}
