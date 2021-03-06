package com.yl.payinterface.core.handle.impl.remit.klt100004.resolve;

import com.thoughtworks.xstream.XStream;
import com.yl.payinterface.core.handle.impl.remit.klt100004.common.dto.impl.Head;
import com.yl.payinterface.core.handle.impl.remit.klt100004.common.dto.impl.Sign;
import com.yl.payinterface.core.handle.impl.remit.klt100004.single.PaymentVersionNo;
import com.yl.payinterface.core.handle.impl.remit.klt100004.single.SinglepayResolveException;
import com.yl.payinterface.core.handle.impl.remit.klt100004.single.dto.impl.query.response.AccontBalanceQueryResponse;
import com.yl.payinterface.core.handle.impl.remit.klt100004.single.dto.impl.query.response.AccountBalanceQueryResponseBody;
import com.yl.payinterface.core.handle.impl.remit.klt100004.single.dto.impl.query.response.AccountBalanceQueryResponseEnvelope;

/**
 * 开联通
 *
 * @author AnLin
 * @version V1.0.0
 * @since 2018/4/3
 */
public class AccountBalanceQueryResponseCustomizeResolver implements ICustomizeResolver {

	public void doCustomizeResolve(XStream xStream) throws SinglepayResolveException {
		xStream.alias("response", AccontBalanceQueryResponse.class);
		xStream.alias("envelope", AccountBalanceQueryResponseEnvelope.class);
		xStream.alias("head", Head.class);
		xStream.alias("body", AccountBalanceQueryResponseBody.class);
		xStream.alias("sign", Sign.class);
	}

	public String[] getSupportedVersionNoAndDirection() {
		return new String[] { PaymentVersionNo.V1078 + ResolverAdapter.DIRECTION_RESPONSE };
	}
}