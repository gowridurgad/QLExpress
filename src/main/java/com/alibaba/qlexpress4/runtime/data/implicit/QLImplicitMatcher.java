package com.alibaba.qlexpress4.runtime.data.implicit;

/**
 * @Author TaoKan
 * @Date 2022/6/29 下午4:24
 */
public class QLImplicitMatcher {
    private final Boolean needImplicitTrans;
    private final Integer matchIndex;
    private QLImplicitVars vars;

    public QLImplicitMatcher(Boolean needImplicitTrans, Integer matchIndex) {
        this.needImplicitTrans = needImplicitTrans;
        this.matchIndex = matchIndex;
        this.vars = new QLImplicitVars(false,0);
    }

    public QLImplicitMatcher(Boolean needImplicitTrans, Integer matchIndex, QLImplicitVars vars) {
        this.needImplicitTrans = needImplicitTrans;
        this.matchIndex = matchIndex;
        this.vars = vars;
    }

    public Boolean needImplicitTrans() {
        return needImplicitTrans;
    }

    public Integer getMatchIndex() {
        return matchIndex;
    }


    public Boolean getNeedImplicitTrans() {
        return needImplicitTrans;
    }

    public QLImplicitVars getVars() {
        return vars;
    }

    public void setVars(QLImplicitVars vars) {
        this.vars = vars;
    }



}