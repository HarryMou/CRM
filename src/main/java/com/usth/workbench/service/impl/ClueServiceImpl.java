package com.usth.workbench.service.impl;

import com.usth.settings.dao.UserDao;
import com.usth.settings.domain.User;
import com.usth.utils.DateTimeUtil;
import com.usth.utils.UUIDUtil;
import com.usth.vo.PaginationVO;
import com.usth.workbench.dao.*;
import com.usth.workbench.domain.*;
import com.usth.workbench.service.ClueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClueServiceImpl implements ClueService {

    @Resource
    private UserDao userDao;

    //线索相关表
    @Resource
    private ClueDao clueDao;
    @Resource
    private ClueActivityRelationDao clueActivityRelationDao;
    @Resource
    private ClueRemarkDao clueRemarkDao;

//    客户相关表
    @Resource
    private CustomerDao customerDao;
    @Resource
    private CustomerRemarkDao customerRemarkDao;

    //联系人相关表
    @Resource
    private ContactsDao contactsDao;
    @Resource
    private ContactsRemarkDao contactsRemarkDao;
    @Resource
    private ContactsActivityRelationDao contactsActivityRelationDao;

    //交易相关表
    @Resource
    private TranDao tranDao;
    @Resource
    private TranHistoryDao tranHistoryDao;

    @Override
    public boolean saveClue(Clue clue) {
        boolean flag =true;
        int count = clueDao.saveClue(clue);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        int total = clueDao.getTotalByCondition(map);
        List<Activity> datalist = clueDao.getClueByCondition(map);

        PaginationVO<Activity> vo = new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(datalist);
        return vo;
    }

    @Override
    public Clue detail(String id) {
        return clueDao.detail(id);
    }

    @Override
    public boolean unbund(String id) {
        boolean flag = true;
        int count = clueActivityRelationDao.unbund(id);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean bund(String[] aid, String cid) {
        boolean flag = true;
        for(String a : aid){
            //取得每一个aid为cid做id
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setActivityId(a);
            car.setClueId(cid);
            //添加关联关系表中的记录
            int count = clueActivityRelationDao.bund(car);
            if(count!=1){
                flag=false;
            }
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran tran,String createBy,String flag) {
        boolean flag1 = true;
        String createTime = DateTimeUtil.getSysTime();

//      通过线索id查询单挑
        Clue clue = clueDao.getClueById(clueId);
//      根据clue提取客户信息，如果客户存在，则不添加，如果客户不存在，则新建客户
        String company = clue.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        if(null ==customer){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(clue.getAddress());
            customer.setWebsite(clue.getWebsite());
            customer.setPhone(clue.getPhone());
            customer.setOwner(clue.getOwner());
            customer.setNextContactTime(clue.getNextContactTime());
            customer.setName(company);
            customer.setDescription(clue.getDescription());
            customer.setCreateTime(createTime);
            customer.setCreateBy(createBy);
            customer.setContactSummary(clue.getContactSummary());
            //添加客户
            int count1 = customerDao.save(clue);
            if(count1!=1){
                flag1=false;
            }
        }
        //客户信息已经拥有了，处理其它表的时候要使用客户id，直接使用getId就行了
        //===========================================================
//        通过线索对象提取联系人信息保存联系人
        Contacts contacts = new Contacts();
        contacts.setId(UUIDUtil.getUUID());
        contacts.setSource(clue.getSource());
        contacts.setOwner(clue.getOwner());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setMphone(clue.getMphone());
        contacts.setJob(clue.getJob());
        contacts.setFullname(clue.getFullname());
        contacts.setEmail(clue.getEmail());
        contacts.setDescription(clue.getDescription());
        contacts.setCustomerId(clue.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setAppellation(clue.getAppellation());
        contacts.setAddress(clue.getAddress());
        //添加联系人
        int count2 = contactsDao.save(contacts);
        if(count2!=1){
            flag1=false;
        }
        //联系人信息已经拥有了，处理其他表的时候就可以使用对象了
        //查询出与线索关联的备注信息表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        for(ClueRemark clueRemark : clueRemarkList){
            //取出备注信息(主要转换到客户备注和联系人备注的是备注信息)
            String noteContent = clueRemark.getNoteContent();
            //创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3!=1){
                flag1=false;
            }
            //创建联系人备注对象，添加联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int count4 = contactsRemarkDao.save(contactsRemark);
            if(count4!=1){
                flag1=false;
            }
        }
        //线索和市场活动的关联关系转换成联系人和市场活动的关联关系
        //查询出与该条线索关联的市场活动
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        for(ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            //从每一条遍历出来的记录中取出关联的市场活动id
            String activityId = clueActivityRelation.getActivityId();
            //创建联系人与市场活动党的关联关系对象
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());
            //添加联系人与市场活动的关联关系
            int count5 = contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5!=1){
                flag1=false;
            }
        }
        //如果有创建交易的需求则创建交易
        if("a".equals(flag)){
            tran.setId(UUIDUtil.getUUID());
            tran.setCreateTime(createTime);
            tran.setCreateBy(createBy);
            //接下来可以通过customer对象取出一些信息
            tran.setSource(clue.getSource());
            tran.setOwner(clue.getOwner());
            tran.setNextContactTime(clue.getNextContactTime());
            tran.setDescription(clue.getDescription());
            tran.setCustomerId(customer.getId());
            tran.setContactSummary(clue.getContactSummary());
            tran.setContactsId(contacts.getId());
            //添加交易
            int count6 = tranDao.save(tran);
            if(count6!=1){
                flag1 = false;
            }
            //如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);
            tranHistory.setTranId(tran.getId());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setStage(tran.getStage());
            //添加交易历史
            int count7 = tranHistoryDao.save(tranHistory);
            if(count7!=1){
                flag1=false;
            }
        }
        //删除线索备注
        for(ClueRemark clueRemark : clueRemarkList){
            int count8 = clueRemarkDao.delete(clueRemark);
            if(count8!=1){
                flag1=false;
            }
        }
        //删除线索和市场活动关联关系
        for(ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if(count9!=1){
                flag1=false;
            }
        }
        //删除线索
        int count10 = clueDao.delete(clue);
        if(count10!=1){
            flag1=false;
        }

        return flag1;
    }

    @Override
    public boolean saveRemark(ClueRemark clueRemark) {
        boolean flag = true;
        int count = clueRemarkDao.saveRemark(clueRemark);
        if(count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public List<ClueRemark> getRemarkListByCId(String clueId) {
        return clueRemarkDao.getListByClueId(clueId);
    }

    @Override
    public boolean deleteClues(String[] ids) {
        boolean flag =true;
        //先查询出需要删除的备注的数量
        int count1 = clueRemarkDao.getCountByCids(ids);
        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = clueRemarkDao.deleteByCids(ids);
        if(count1!=count2){
            flag = false;
        }
        //查询线索关联的市场活动的数量
        int count4 = clueActivityRelationDao.getCountByCids(ids);
        //删除关联信息，返回受影响的条数
        int count5 = clueActivityRelationDao.deleteByCids(ids);
        if(count4!=count5){
            flag=false;
        }
        //删除市场活动
        int count3 = clueDao.deleteClues(ids);
        if(count3!=ids.length){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndClue(String clueId) {
        List<User> userList = userDao.getUserList();
        Clue clue = clueDao.getClueById(clueId);
        Map<String, Object> map = new HashMap<>();
        map.put("userList",userList);
        map.put("clue",clue);
        return map;
    }

    @Override
    public boolean updateClue(Clue clue) {
        boolean flag = true;
        int count = clueDao.updateClue(clue);
        if(count!=1){
            flag=false;
        }
        return flag;
    }


}
