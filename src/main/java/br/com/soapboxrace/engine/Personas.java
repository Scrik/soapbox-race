package br.com.soapboxrace.engine;

import br.com.soapboxrace.bo.BasketBO;
import br.com.soapboxrace.bo.PersonaBO;
import br.com.soapboxrace.definition.ServerExceptions;
import br.com.soapboxrace.definition.ServerExceptions.PersonaIdMismatchException;
import br.com.soapboxrace.jaxb.BasketTransType;
import br.com.soapboxrace.jaxb.CarSlotInfoTrans;
import br.com.soapboxrace.jaxb.CommerceResultTransType;
import br.com.soapboxrace.jaxb.CommerceSessionResultTransType;
import br.com.soapboxrace.jaxb.CommerceSessionTransType;
import br.com.soapboxrace.jaxb.util.MarshalXML;
import br.com.soapboxrace.jaxb.util.UnmarshalXML;
import br.com.soapboxrace.jpa.OwnedCarEntity;

public class Personas extends Router {

	private PersonaBO personaBO = new PersonaBO();

	private long getPersonaId(boolean isBypass) throws PersonaIdMismatchException {
		String[] targetSplitted = getTarget().split("/");
		Long idPersona = Long.valueOf(targetSplitted[4]);
		if (((isBypass || idPersona.equals(getLoggedPersonaId()) || getLoggedPersonaId() == -1L)))
			if (getUserId() != -1L && getSecurityToken() != null
					&& Router.activeUsers.get(getUserId()).getSecurityToken().equals(getSecurityToken()))
				return idPersona;
		throw new ServerExceptions.PersonaIdMismatchException(getLoggedPersonaId(), idPersona);
	}

	private long getPersonaId() throws PersonaIdMismatchException {
		return getPersonaId(false);
	}

	private long getDefaultCarId() {
		long carId = 0;
		String[] targetSplitted = getTarget().split("/");
		if (targetSplitted.length == 7) {
			carId = Long.valueOf(targetSplitted[6]);
		}
		return carId;
	}

	public String carslots() throws PersonaIdMismatchException {
		CarSlotInfoTrans carslots = personaBO.carslots(getPersonaId());
		System.out.println(MarshalXML.marshal(carslots));
		return MarshalXML.marshal(carslots);
	}

	public String inventory() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<InventoryTrans>\n");
		stringBuilder.append("<InventoryItems>\n");
		stringBuilder.append("<InventoryItemTrans>\n");
		stringBuilder.append("<EntitlementTag>nosshot</EntitlementTag>\n");
		stringBuilder.append("<ExpirationDate i:nil=\"true\" />\n");
		stringBuilder.append("<Hash>-1681514783</Hash>\n");
		stringBuilder.append("<InventoryId>1842996427</InventoryId>\n");
		stringBuilder.append("<ProductId>DO NOT USE ME</ProductId>\n");
		stringBuilder.append("<RemainingUseCount>100</RemainingUseCount>\n");
		stringBuilder.append("<ResellPrice>0.00000</ResellPrice>\n");
		stringBuilder.append("<Status>ACTIVE</Status>\n");
		stringBuilder.append("<StringHash>0x9bc61ee1</StringHash>\n");
		stringBuilder.append("<VirtualItemType>powerup</VirtualItemType>\n");
		stringBuilder.append("</InventoryItemTrans>\n");
		stringBuilder.append("</InventoryItems>\n");
		stringBuilder.append("</InventoryTrans>");
		String inventoryStr = stringBuilder.toString();
		return inventoryStr;
	}

	public String defaultcar() throws PersonaIdMismatchException {
		long personaId = getPersonaId(true);
		long defaultCarId = getDefaultCarId();
		if (defaultCarId != 0) {
			personaBO.changeDefaultCar(personaId, defaultCarId);
			return "";
		}
		OwnedCarEntity ownedCarEntity = personaBO.defaultcar(personaId);
		if (ownedCarEntity != null) {
			return MarshalXML.marshal(ownedCarEntity);
		}
		return "";
	}

	public String commerce() throws PersonaIdMismatchException {
		String commerceXml = readInputStream();
		CommerceSessionTransType commerceSessionTransType = new CommerceSessionTransType();
		commerceSessionTransType = (CommerceSessionTransType) UnmarshalXML.unMarshal(commerceXml,
				commerceSessionTransType);
		CommerceSessionResultTransType commerceSessionResultTrans = personaBO.commerce(getPersonaId(),
				commerceSessionTransType.getUpdatedCar());
		return MarshalXML.marshal(commerceSessionResultTrans);
	}

	public String baskets() throws PersonaIdMismatchException {
		String basketXml = readInputStream();
		BasketTransType basketTransType = new BasketTransType();
		basketTransType = (BasketTransType) UnmarshalXML.unMarshal(basketXml, basketTransType);
		String productId = basketTransType.getItems().getBasketItemTrans().getProductId();
		BasketBO basketBO = new BasketBO();
		CommerceResultTransType commerceResultTrans = basketBO.basket(getPersonaId(), productId);
		return MarshalXML.marshal(commerceResultTrans);
	}

	public String cars() throws PersonaIdMismatchException {
		String serialNumber = getParam("serialNumber");
		if (serialNumber != null) {
			Long carId = Long.valueOf(serialNumber);
			OwnedCarEntity defaultCar = personaBO.deleteCar(getPersonaId(), carId);
			return MarshalXML.marshal(defaultCar);
		}
		String ownedCarTransXml = readInputStream();
		System.out.println("TODO: sell performance shop");
		System.out.println(ownedCarTransXml);
		return "";
	}
}
