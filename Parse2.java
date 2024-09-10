import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class XMLParserWithUnionAll {

    public static void main(String[] args) {
        try {
            // Sample XML content
            String xmlContent = "<Inventory>" +
                                "  <Section type='type1'>" +
                                "    <Component name='component1'>" +
                                "      <Component name='subcomponent1'>" +
                                "        <ValueItem>" +
                                "          <Name>Item1</Name>" +
                                "          <Value>Value1</Value>" +
                                "        </ValueItem>" +
                                "      </Component>" +
                                "    </Component>" +
                                "    <Component name='component2'>" +
                                "      <Component name='subcomponent2'>" +
                                "        <ValueItem>" +
                                "          <Name>Item2</Name>" +
                                "          <Value>Value2</Value>" +
                                "        </ValueItem>" +
                                "        <ValueItem>" +
                                "          <Name>Item3</Name>" +
                                "          <Value>Value3</Value>" +
                                "        </ValueItem>" +
                                "      </Component>" +
                                "    </Component>" +
                                "  </Section>" +
                                "</Inventory>";

            // Convert XML string to Document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new java.io.ByteArrayInputStream(xmlContent.getBytes()));

            // Create XPath
            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            // Extract Section nodes
            XPathExpression sectionExpr = xpath.compile("/Inventory/Section");
            NodeList sectionNodes = (NodeList) sectionExpr.evaluate(document, XPathConstants.NODESET);

            // Iterate over Section nodes
            for (int i = 0; i < sectionNodes.getLength(); i++) {
                Element section = (Element) sectionNodes.item(i);
                String sectionType = section.getAttribute("type");
                System.out.println("Section Type: " + sectionType);

                // Extract Component nodes
                XPathExpression componentExpr = xpath.compile("./Component");
                NodeList componentNodes = (NodeList) componentExpr.evaluate(section, XPathConstants.NODESET);

                for (int j = 0; j < componentNodes.getLength(); j++) {
                    Element component = (Element) componentNodes.item(j);
                    String componentName = component.getAttribute("name");
                    System.out.println("  Component Name: " + componentName);

                    // Extract Subcomponent nodes
                    XPathExpression subcomponentExpr = xpath.compile("./Component");
                    NodeList subcomponentNodes = (NodeList) subcomponentExpr.evaluate(component, XPathConstants.NODESET);

                    for (int k = 0; k < subcomponentNodes.getLength(); k++) {
                        Element subcomponent = (Element) subcomponentNodes.item(k);
                        String subcomponentName = subcomponent.getAttribute("name");
                        System.out.println("    Subcomponent Name: " + subcomponentName);

                        // Extract ValueItem nodes from the subcomponent
                        XPathExpression valueItemExpr = xpath.compile("./ValueItem");
                        NodeList valueItemNodes = (NodeList) valueItemExpr.evaluate(subcomponent, XPathConstants.NODESET);

                        for (int l = 0; l < valueItemNodes.getLength(); l++) {
                            Element valueItem = (Element) valueItemNodes.item(l);
                            String itemName = valueItem.getElementsByTagName("Name").item(0).getTextContent();
                            String itemValue = valueItem.getElementsByTagName("Value").item(0).getTextContent();
                            System.out.println("      Item Name: " + itemName);
                            System.out.println("      Item Value: " + itemValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
