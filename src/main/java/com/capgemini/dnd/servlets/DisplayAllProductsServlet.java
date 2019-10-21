package com.capgemini.dnd.servlets;

 

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 

import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.util.MappingUtil;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMessages;

 

public class DisplayAllProductsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

 

    public DisplayAllProductsServlet() {
        super();
    }

 

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

 

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

 

        doGet(req, res);
        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");
        res.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
        PrintWriter out = res.getWriter();
        String jsonMessage = "";
        String errorMessage = "";

 

        List<ProductOrder> poList = new ArrayList<ProductOrder>();
        ProductService productServiceObject = new ProductServiceImpl();
        DisplayProductOrder displayProductOrderObject = new DisplayProductOrder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

 

        Map<String, String> fieldValueMap = new HashMap<String, String>();
        fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(req);

 

        String DeliveryStatusVar = fieldValueMap.get("deliveryStatus");
        String DistributorIDVar = fieldValueMap.get("distributorid");
        String date1Var = fieldValueMap.get("startdate");
        String date2Var = fieldValueMap.get("endDate");
        System.out.println(date1Var);
        System.out.println(date2Var);
        

 

        displayProductOrderObject.setDeliveryStatus(DeliveryStatusVar);
        displayProductOrderObject.setDistributorid(DistributorIDVar);
        displayProductOrderObject.setStartdate(date1Var);
        displayProductOrderObject.setEndDate(date2Var);

 

        try {
            jsonMessage = productServiceObject.displayProductOrders(displayProductOrderObject);
            
        } catch (Exception e) {

 

            errorMessage = e.getMessage();
        }
        if (errorMessage.isEmpty()) {
            out.write(jsonMessage);
        } 
 

        
    }
}
