package org.daniel.dictqueryweb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daniel.dictquery.query.QueryService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private BundleContext context;

	public QueryServlet(BundleContext context) {
		this.context = context;
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		// 读取Request参数
		String queryWord = request.getParameter("query_word");
		response.setContentType("text/html");
		ServletOutputStream output = response.getOutputStream();

		// 获取服务
		QueryService queryService = null;
		ServiceReference<?> serviceRef = context
				.getServiceReference(QueryService.class.getName());
		if (null != serviceRef) {
			queryService = (QueryService) context.getService(serviceRef);
		}

		if (queryService == null) {
			output.println("No available dictquery service");
			output.close();
			return;
		}

		try {
			output.println("Result is " + queryService.queryWord(queryWord));
			output.close();
			return;
		} catch (Exception e) {
			output.println("Error occurs");
			output.println(e.toString());
			output.close();
			return;
		}
	}
}
