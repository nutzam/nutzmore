/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.nutz.shiro.ext.aop;

import java.lang.annotation.Annotation;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.subject.Subject;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.shiro.InstallPermission;
import org.nutz.shiro.ext.anno.ThunderRequiresPermissions;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
public class ThunderPermissionAnnotationHandler extends PermissionAnnotationHandler {

	/**
	 * Default no-argument constructor that ensures this handler looks for
	 * {@link org.apache.shiro.authz.annotation.NutzRequiresPermissionss
	 * NutzRequiresPermissionss} annotations.
	 */
	public ThunderPermissionAnnotationHandler() {
		setAnnotationClass(ThunderRequiresPermissions.class);
	}

	/**
	 * Ensures that the calling <code>Subject</code> has the Annotation's
	 * specified permissions, and if not, throws an
	 * <code>AuthorizingException</code> indicating access is denied.
	 *
	 * @param a
	 *            the NutzRequiresPermissions annotation being inspected to
	 *            check for one or more permissions
	 * @throws org.apache.shiro.authz.AuthorizationException
	 *             if the calling <code>Subject</code> does not have the
	 *             permission(s) necessary to continue access or execution.
	 */
	@Override
	public void assertAuthorized(Annotation a) throws AuthorizationException {
		if (!(a instanceof ThunderRequiresPermissions))
			return;

		ThunderRequiresPermissions rpAnnotation = (ThunderRequiresPermissions) a;
		InstallPermission[] perms_ = rpAnnotation.value();
		Subject subject = getSubject();

		final String[] perms = new String[perms_.length];

		Lang.each(perms_, new Each<InstallPermission>() {

			@Override
			public void invoke(int index, InstallPermission ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				perms[index] = ele.getName();
			}
		});

		if (perms.length == 1) {
			subject.checkPermission(perms[0]);
			return;
		}
		if (Logical.AND.equals(rpAnnotation.logical())) {
			getSubject().checkPermissions(perms);
			return;
		}
		if (Logical.OR.equals(rpAnnotation.logical())) {
			boolean hasAtLeastOnePermission = false;
			for (String permission : perms)
				if (getSubject().isPermitted(permission))
					hasAtLeastOnePermission = true;
			if (!hasAtLeastOnePermission)
				getSubject().checkPermission(perms[0]);
		}
	}
}
