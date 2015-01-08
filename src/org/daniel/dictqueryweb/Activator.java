package org.daniel.dictqueryweb;

import javax.servlet.Servlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

public class Activator implements BundleActivator, ServiceListener {

	private static BundleContext context;
	private Servlet servlet = null;
	private ServiceReference<?> reference = null;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		servlet = new QueryServlet(bundleContext);
		registerServlete();
		context.addServiceListener(this,
				"(objectclass=" + HttpService.class.getName() + ")");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	public void registerServlete() {
		if (reference == null) {
			reference = context
					.getServiceReference(HttpService.class.getName());
		}
		if (reference != null) {
			try {
				HttpService http = (HttpService) context.getService(reference);
				if (null != http) {
					http.registerServlet("/demo/dictquery", servlet, null, null);
					http.registerResources("/demo/page", "page", null);
					System.out
							.println("已启动字典查询web模块，请通过/demo/page/dictquery.htm访问");
				}
			} catch (Exception e) {

			}
		}
	}

	public void unregisterServlete() {
		if (reference != null) {
			try {
				HttpService http = (HttpService) context.getService(reference);
				if (null != http) {
					http.unregister("/demo/dictquery");
					http.unregister("/demo/page");
					System.out.println("已卸载字典查询web模块！");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		switch (event.getType()) {
		case ServiceEvent.REGISTERED:
			// HttpService注册到OSGi容器的时候，进行Servlet的注册
			registerServlete();
			break;

		case ServiceEvent.UNREGISTERING:
			// HttpService从OSGi容器注销的时候，注销Servlet
			unregisterServlete();
			break;
		}

	}

}
