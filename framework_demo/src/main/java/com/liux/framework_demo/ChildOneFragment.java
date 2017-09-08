package com.liux.framework_demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liux.framework.base.BaseFragment;
import com.liux.framework.pay.PayTool;
import com.liux.framework.pay.alipay.AliRequest;
import com.liux.framework.pay.alipay.AliResult;
import com.liux.framework.pay.unionpay.UnionRequest;
import com.liux.framework.pay.wxpay.WxRequest;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.modelpay.PayResp;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Liux on 2017/8/13.
 */

public class ChildOneFragment extends BaseFragment {

    Unbinder unbinder;

    @Override
    protected void onInitData(Bundle savedInstanceState) {
        PayTool.DEBUG = true;
    }

    @Override
    protected View onInitView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_one, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onLazyLoad() {

    }

    @Override
    protected void onRestoreData(Bundle data) {

    }

    @Override
    protected void onSaveData(Bundle data) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_ali, R.id.btn_wx, R.id.btn_union})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_ali:
                PayTool.DEBUG = true;
                PayTool.with(getActivity())
                        .request(new AliRequest("") {
                            @Override
                            public void callback(AliResult aliResult) {
                                String result = aliResult.getResultStatus();
                                switch (result) {
                                    case ALI_MEMO_SUCCEED:
                                        Toast.makeText(getContext(), "支付完成", Toast.LENGTH_SHORT).show();
                                        break;
                                    case ALI_MEMO_UNDERWAY:
                                        Toast.makeText(getContext(), "支付处理中", Toast.LENGTH_SHORT).show();
                                        break;
                                    case ALI_MEMO_FAILURE:
                                        Toast.makeText(getContext(), "支付失败", Toast.LENGTH_SHORT).show();
                                        break;
                                    case ALI_MEMO_CANCEL:
                                        Toast.makeText(getContext(), "取消支付", Toast.LENGTH_SHORT).show();
                                        break;
                                    case ALI_MEMO_REPEAT:
                                        Toast.makeText(getContext(), "重复支付", Toast.LENGTH_SHORT).show();
                                        break;
                                    case ALI_MEMO_ERROR:
                                        Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                                        break;
                                    case ALI_MEMO_UNKNOWN:
                                        Toast.makeText(getContext(), "支付状态未知", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(getContext(), "支付错误 [" + result + "]", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        })
                        .pay();
                break;
            case R.id.btn_wx:
                PayTool.DEBUG = true;
                PayTool.with(getActivity())
                        .request(new WxRequest(new PayReq()) {
                            @Override
                            public void callback(PayResp payResp) {
                                switch (payResp.errCode) {
                                    case ERR_PARAM:
                                        Toast.makeText(getContext(), "参数错误", Toast.LENGTH_SHORT).show();
                                        break;
                                    case ERR_VERSION:
                                        Toast.makeText(getContext(), "微信客户端未安装或版本过低", Toast.LENGTH_SHORT).show();
                                        break;
                                    case PayResp.ErrCode.ERR_OK:
                                        Toast.makeText(getContext(), "支付完成", Toast.LENGTH_SHORT).show();
                                        break;
                                    case PayResp.ErrCode.ERR_COMM:
                                        Toast.makeText(getContext(), "支付错误", Toast.LENGTH_SHORT).show();
                                        break;
                                    case PayResp.ErrCode.ERR_USER_CANCEL:
                                        Toast.makeText(getContext(), "取消支付", Toast.LENGTH_SHORT).show();
                                        break;
                                    case PayResp.ErrCode.ERR_SENT_FAILED:
                                    case PayResp.ErrCode.ERR_AUTH_DENIED:
                                    case PayResp.ErrCode.ERR_UNSUPPORT:
                                    case PayResp.ErrCode.ERR_BAN:
                                    default:
                                        Toast.makeText(getContext(), "支付错误 [" + payResp.errStr + "]", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        })
                        .pay();
                break;
            case R.id.btn_union:
                PayTool.with(getActivity())
                        .request(new UnionRequest("") {
                            @Override
                            public void callback(Object object) {
                                Toast.makeText(getContext(), "暂未支持", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .pay();
                break;
        }
    }
}