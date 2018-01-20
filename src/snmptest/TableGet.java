package snmptest;

import java.io.IOException;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;

import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableUtils;

import org.snmp4j.util.TableEvent;


public class TableGet {

	public static final int DEFAULT_VERSION = SnmpConstants.version2c;
	public static final String DEFAULT_PROTOCOL = "udp";

	public static final int DEFAULT_PORT = 161;
	public static final long DEFAULT_TIMEOUT = 3 * 1000L;
	public static final int DEFAULT_RETRY = 3;

	/**
	 * 创建对象communtiyTarget
	 * 
	 * 
	 * @param tartgetAddress
	 * @param community
	 * @param version
	 * @param timeout
	 * @param retry
	 * 
	 * @return CommunityTarget
	 * 
	 */

	public static CommunityTarget createDefault(String ip, String community) {
		Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip + "/" + DEFAULT_PORT);

		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(address);
		target.setVersion(DEFAULT_VERSION);
		target.setTimeout(DEFAULT_TIMEOUT);
		target.setRetries(DEFAULT_RETRY);

		return target;

	}

	public static void snmpTableGet(String ip, String community, String oid) {
		CommunityTarget target = createDefault(ip, community);
		Snmp snmp = null;
		try {
			PDU pdu = new PDU();

			// pdu.add(new VariableBinding(new OID(new int[] {1,3,6,2,1,1,2})));

			pdu.add(new VariableBinding(new OID(oid)));

			DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
			
			//建立snmp对象或是Session.
			snmp = new Snmp(transport);
			snmp.listen();
			System.out.println("----------发送PDU-----------");

			//建立Table信息获取工具
			TableUtils utils = new TableUtils(snmp, new DefaultPDUFactory(PDU.GETBULK));
			
			//设置最大每个PDU返回行数，实测不影响返回数据
			utils.setMaxNumRowsPerPDU(3);
			
			//设置要查询的OID
			//OID[] columnOids = new OID[]{new OID("1.3.6.1.2.1.2.2.1.1")};
			OID[] columnOids = new OID[]{new OID(oid)};
			
			//获取表格型snmp数据
			List<TableEvent> I=utils.getTable(target, columnOids, null, null);
			
			//测试getDenseTable 据说可以返回一个完整的列表而不是消息。userObject是什么还要了解。 
			//darpTable = utils.getDenseTable(target, columnOids, listener, darpTable, null, null);
			
			//显示表格型snmp数据
			for(TableEvent x:I) {
				System.out.println(x.getIndex().toString()+" : "+x.getColumns()[0].toString());
/*				for(VariableBinding vb:x.getColumns()) {
					System.out.println(" ***  "+vb.getVariable().toString());
				}*/
			}
						
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("SNMP Table Get Exception:" + e);
		} finally {
			if (snmp != null) {
				try {
					snmp.close();

				} catch (IOException ex1) {
					snmp = null;
					// TODO: handle exception
				}
			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ip = "192.168.16.29";
		String community = "sensor";
		//String oidval = "1.3.6.1.2.1.2.2.1.1";
		String oidval = "1.3.6.1.2.1.4.22.1.2";
		snmpTableGet(ip, community, oidval);

	}

}