package org.lptr.otp2qif

import spock.lang.Specification

class Otp2QifTest extends Specification {
	def "convert"() {
		StringWriter output = new StringWriter();
		Otp2Qif.convert(new File("src/test/data/export.xls"), output)
		expect:
		output.toString() == """!Type:Bank
D2014.08.28
T0
POTP BANK NYRT.
M5739457943759873489
^
D2014.08.28
T33980
PBELA Bt.
M22945670-13634533-33180003 00000000000000000000000000000000016
^
D2014.08.28
T-19900
P
MSERVICE COMPANY  2014.08.26 74274923744
^
D2014.09.01
T-946
PABC BT.
Mwhfui 47593475894354  47573489759843795534344
^
D2014.09.01
T-6458
PBUDAPESTI ELEKTROMOS MŰVEK NYRT.
M7537495789  53987454835
^
"""
	}
}
