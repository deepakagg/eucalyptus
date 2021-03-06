/*************************************************************************
 * (c) Copyright 2016 Hewlett Packard Enterprise Development Company LP
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 ************************************************************************/
package com.eucalyptus.portal.awsusage;

import com.eucalyptus.bootstrap.Bootstrap;
import com.eucalyptus.event.Listeners;
import com.eucalyptus.portal.BillingProperties;
import com.eucalyptus.reporting.event.AddressEvent;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;

public class AddressUsageEventListener extends SensorQueueEventListener<AddressEvent> {
  private static final Logger LOG = Logger.getLogger(AddressUsageEventListener.class);

  public static void register() {
    Listeners.register(AddressEvent.class, new AddressUsageEventListener());
  }

  @Override
  public void fireEvent(@Nonnull final AddressEvent event) {
    // should run in the same host running swf activities
    if (!Bootstrap.isOperational() || !BillingProperties.ENABLED) {
      return;
    }
    if (event.getActionInfo() == null)
      return;

    final AddressEvent.AddressAction action = event.getActionInfo().getAction();
    if (!(AddressEvent.AddressAction.USAGE_ALLOCATE.equals(action) ||
            AddressEvent.AddressAction.USAGE_ASSOCIATE.equals(action)))
      return;

    transformAndQueue( LOG, event, QueuedEvents.FromAddressUsageEvent );
  }
}